package com.library.controller;

import com.library.dto.BookDTO;
import com.library.dto.request.BorrowRequest;
import com.library.dto.request.ReturnRequest;
import com.library.entity.Transaction;
import com.library.mappers.BookMapper;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.service.BookService;
import com.library.service.TransactionService;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/addbook")
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        return bookService.addBook(bookDTO);
    }

    @PostMapping("/borrow")
    public BookDTO borrowBook(@RequestBody BorrowRequest request) {
        Long userId = request.userId();
        Long bookId = request.bookId();
        System.out.println(userId);
        bookService.borrowBook(userId, bookId);
        return null;
    }

    @PutMapping("/return")
    public BookDTO returnBook(@RequestBody BorrowRequest request) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(request.Id());
        Transaction transaction = optionalTransaction.get();
        Long borrowId = transaction.getId();
        System.out.println(borrowId);
        transactionRepository.findById(borrowId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        bookService.returnBook(borrowId);
        return null;
    }

    /* TODO create borrow and return methods */


}
