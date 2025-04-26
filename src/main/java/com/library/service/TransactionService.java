package com.library.service;


import com.library.entity.Transaction;
import com.library.entity.User;
import com.library.entity.enums.ActionType;
import com.library.repository.TransactionRepository;
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

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;

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

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    public Optional<Transaction> hasActiveTransaction(Long Id) {
        return transactionRepository.findById(Id);
    }

}