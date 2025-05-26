package com.library.controller;

import com.library.dto.BookDTO;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.entity.Transaction;
import com.library.mappers.BookMapper;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.service.BookService;
import com.library.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private TransactionRepository transactionRepository;

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
    public void borrowBook(@RequestBody BorrowRequest request) {
        bookService.borrowBook(request.userId(), request.bookId());
    }

    @PutMapping("/return")
    public void returnBook(@RequestBody BorrowRequest request) {
        Transaction transaction = transactionRepository.findById(request.Id())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        bookService.returnBook(transaction.getId());
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

