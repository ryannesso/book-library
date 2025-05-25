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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@RequestMapping("/api/books")
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

    @PostMapping("/create_book")
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        System.out.println("createBook() called with: " + bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        if(book.getAvailableCopies() < 0){
            throw new IllegalArgumentException("Available books cannot be negative");
        }
        return bookService.addBook(bookDTO);
    }

    @PostMapping("/borrow_book")
    public BookDTO borrowBook(@RequestBody BorrowRequest request) {
        Long userId = request.userId();
        Long bookId = request.bookId();
        System.out.println(userId);
        bookService.borrowBook(userId, bookId);
        return null;
    }

    @PutMapping("/return_book")
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
