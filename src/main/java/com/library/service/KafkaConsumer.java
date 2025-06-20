package com.library.service;

import com.library.dto.request.transactionalRequest.BookActionEvent;
import com.library.entity.Book;
import com.library.entity.Transaction;
import com.library.entity.enums.ActionType;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumer {

    @Autowired private BookRepository bookRepository;
    @Autowired private TransactionService transactionService;
    @Autowired  private TransactionRepository transactionRepository;
    @KafkaListener(topics = "book-actions", groupId = "library-group")
    public void handleBookAction(BookActionEvent event) {
        if ("RETURN".equalsIgnoreCase(event.actionType())) {
            var transaction = transactionRepository
                    .findByUserIdAndBookIdAndIsActiveTrue(event.userId(), event.bookId())
                    .orElseThrow(() -> new RuntimeException("No active transaction"));

            // Get the book
            Book book = bookRepository.findById(transaction.getBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));


            transactionService.updateTransaction(transaction.getId());
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        } else if ("BORROW".equalsIgnoreCase(event.actionType())) {
            Optional<Transaction> transactions = transactionRepository.findByUserIdAndBookIdAndIsActiveTrue(event.userId(), event.bookId());
            if (transactions.isPresent()) {
                throw new IllegalArgumentException("you already have an active transaction for this book");
            }

            Optional<Book> bookOptional = bookRepository.findById(event.bookId());
            if (bookOptional.isEmpty()) {
                throw new IllegalArgumentException("book not found");
            }
            Book book = bookOptional.get();
            if (book.getAvailableCopies() <= 0) {
                throw new IllegalStateException("No copies of the book are currently available");
            }

            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
            transactionService.addTransaction(event.userId(), event.bookId(), ActionType.BORROW);
        }
    }
}
