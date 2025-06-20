package com.library.controller;


import com.library.config.JwtAuthenticationFilter;
import com.library.config.JwtService;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.dto.request.transactionalRequest.ReturnRequest;
import com.library.entity.Transaction;
import com.library.repository.TransactionRepository;
import com.library.service.BookService;
import com.library.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final BookService bookService;
    private final TransactionRepository transactionRepository;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtService jwtService;

    public TransactionController(
            TransactionService transactionService,
            BookService bookService,
            TransactionRepository transactionRepository,
            JwtAuthenticationFilter jwtFilter,
            JwtService jwtService
    ) {
        this.transactionService = transactionService;
        this.bookService = bookService;
        this.transactionRepository = transactionRepository;
        this.jwtFilter = jwtFilter;
        this.jwtService = jwtService;
    }



    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtFilter.extractTokenFromRequest(httpRequest);
        Long userId = jwtService.extractUserId(jwt);
        transactionService.borrowBook(userId, request.bookId());
        return ResponseEntity.ok().build();

    }

    @PutMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtFilter.extractTokenFromRequest(httpRequest);
        Long userId = jwtService.extractUserId(jwt);
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndBookIdAndIsActiveTrue(userId, request.bookId());
        if (transaction.isEmpty()) {
            throw new IllegalArgumentException("transaction not found");
        }
        transactionService.returnBook(transaction.get().getId());
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/transaction")
//    public Transaction createTransaction(@RequestBody Transaction transaction) {
//        return transactionService.addTransaction(transaction);
//    }



}