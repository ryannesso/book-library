package com.library.controller;

import com.library.config.JwtAuthenticationFilter;
import com.library.config.JwtService;
import com.library.dto.BookDTO;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.dto.request.transactionalRequest.ReturnRequest;
import com.library.entity.Transaction;
import com.library.repository.TransactionRepository;
import com.library.service.BookService;
import com.library.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private TransactionService transactionService;
    @Autowired private JwtAuthenticationFilter jwtFilter;
    @Autowired private JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public List<BookDTO> createBook(@RequestBody List<BookDTO> bookDTOs) {
        for (BookDTO dto : bookDTOs) {
            if(dto.availableCopies() < 0) {
                throw new IllegalArgumentException("cannot be negative");
            }
        }
        return bookService.addBooks(bookDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtFilter.extractTokenFromRequest(httpRequest);
        Long userId = jwtService.extractUserId(jwt);

        bookService.borrowBook(userId, request.bookId());
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
        bookService.returnBook(transaction.get().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/title")
    public List<BookDTO> getBooksByTitle(@RequestParam String title) {
        return bookService.getBookByTitle(title);
    }

    @GetMapping("/all")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }
}

