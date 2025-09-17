package com.library.controller;


import com.library.config.JwtAuthenticationFilter;
import com.library.config.JwtService;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.dto.request.transactionalRequest.ReturnRequest;
import com.library.entity.Transaction;
import com.library.repository.TransactionRepository;
import com.library.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtService jwtService;

    public TransactionController(
            TransactionService transactionService,
            TransactionRepository transactionRepository,
            JwtAuthenticationFilter jwtFilter,
            JwtService jwtService
    ) {
        this.transactionService = transactionService;
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

    @GetMapping("/transactions/filter")
    public ResponseEntity<List<Transaction>> filterTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long userId) {
        List<Transaction> filtered;
        if (startDate != null && endDate != null && userId != null) {
            // Полная фильтрация
            filtered = transactionRepository.findByBorrowDateBetweenAndUserId(startDate, endDate, userId);
        } else if (startDate != null && endDate != null) {
            // Только по дате
            filtered = transactionRepository.findByBorrowDateBetween(startDate, endDate);
        } else if (userId != null) {
            // Только по userId (используй findByUserId, если добавишь в репозиторий)
            filtered = transactionRepository.findByUserId(userId);  // Добавь этот метод в репозиторий, если нет
        } else {
            // Все транзакции
            filtered = transactionRepository.findAll();
        }
        return ResponseEntity.ok(filtered);
    }





}