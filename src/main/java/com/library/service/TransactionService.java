package com.library.service;


import com.library.dto.request.transactionalRequest.BookActionEvent;
import com.library.entity.Transaction;
import com.library.entity.User;
import com.library.entity.enums.ActionType;
import com.library.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private  final TransactionRepository transactionRepository;
    private final KafkaProducer kafkaProducer;
    private final UserService userService;
    private final BookService bookService;

    public TransactionService(TransactionRepository transactionRepository, KafkaProducer kafkaProducer, UserService userService, BookService bookService) {
        this.transactionRepository = transactionRepository;
        this.kafkaProducer = kafkaProducer;
        this.userService = userService;
        this.bookService = bookService;
    }

    public Transaction addTransaction(Long userId, Long bookId, ActionType actionType) {
        User user = userService.getUserById(userId);
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setBookId(bookId);
        transaction.setActionType(actionType);
        transaction.setBorrowDate(LocalDateTime.now());

        if(actionType == ActionType.BORROW){
            transaction.setActive(true);
        }
        else if(actionType == ActionType.RETURN){
            transaction.setActive(false);
        }
        return transactionRepository.save(transaction);

        //todo maybe rewrite this function for function that receive user_id and book_id
    }

    public void updateTransaction(Long borrowId) {
        Transaction transaction = transactionRepository.findById(borrowId).get();
        transaction.setActive(false);
        transaction.setReturnDate(LocalDateTime.now());
        transaction.setActionType(ActionType.RETURN);
        transactionRepository.save(transaction);
    }

    public void borrowBook(Long userId, Long bookId) {
        if( bookId == null || userId == null) {
            throw new  IllegalArgumentException("id must be not null");
        }
        int userCredits = userService.getCredits(userId);
        int bookPrice =  bookService.getPrice(bookId);
        if(userCredits < bookPrice) {
            throw new IllegalArgumentException("Not enough credits");
        }
        if(bookService.getStatus(bookId)) {
            userService.subtractCredits(userId, bookPrice);
            userService.addBorrowCount(userId);
            BookActionEvent event = new BookActionEvent(userId, bookId, "BORROW");
            kafkaProducer.sendBookAction(event);
        }
        else {
            System.out.println("Book not available");
        }

    }

    public void returnBook(Long borrowId) {
        Optional<Transaction> OpTransaction = transactionRepository.findById(borrowId);
        Transaction transaction = OpTransaction.orElseThrow(() -> new EntityNotFoundException("transaction not found"));
        Long userId = transaction.getUserId();
        Long bookId = transaction.getBookId();
        int bookPrice = bookService.getPrice(bookId);
        userService.addCredits(userId, bookPrice);
        userService.subtractBorrowCount(userId);
        BookActionEvent event = new BookActionEvent(userId, bookId, "RETURN");
        kafkaProducer.sendBookAction(event);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}