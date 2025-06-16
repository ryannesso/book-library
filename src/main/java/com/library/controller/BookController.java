package com.library.controller;

import com.library.config.JwtAuthenticationFilter;
import com.library.config.JwtService;
import com.library.dto.BookDTO;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.dto.request.transactionalRequest.ReturnRequest;
import com.library.entity.Transaction;
import com.library.mappers.BookMapper;
import com.library.entity.Book;
import com.library.repository.BookRepository;
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
    @Autowired private JwtAuthenticationFilter jwtFilter;
    @Autowired private JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        if (bookDTO.availableCopies() < 0) {
            throw new IllegalArgumentException("Available books cannot be negative");
        }
        return bookService.addBook(bookDTO);
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
    public ResponseEntity<?> returnBook(@RequestBody ReturnRequest request) {
        Transaction transaction = transactionRepository.findById(request.Id())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        bookService.returnBook(transaction.getId());
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

