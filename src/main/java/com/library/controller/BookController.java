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

