package com.library.controller;


import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.mappers.BookMapper;
import com.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {


    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        if(bookDTO.availableCopies() < 0) {
            throw new IllegalArgumentException("cannot be negative");
        }
        Book book = bookService.addBooks(bookMapper.toEntity(bookDTO));

        return bookMapper.toDTO(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @RequestBody BookDTO updatedBookDTO
    ) {

        Book updatedBook = bookMapper.toEntity(updatedBookDTO);
        bookService.updateBook(id, updatedBook);
        return ResponseEntity.ok(bookMapper.toDTO(updatedBook));
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return BookMapper.MAPPER.toDTO(bookService.getBookById(id));
    }

    @GetMapping("/title")
    public List<BookDTO> getBooksByTitle(@RequestParam String title) {
        return bookMapper.toDTO(bookService.getBookByTitle(title));
    }

    @GetMapping("/all")
    public List<BookDTO> getAllBooks() {
        return bookMapper.toDTO(bookService.getAllBooks());
    }
}

