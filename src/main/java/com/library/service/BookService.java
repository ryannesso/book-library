package com.library.service;

import com.library.entity.Transaction;
import com.library.entity.enums.ActionType;
import com.library.mappers.BookMapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;


    public List<BookDTO> addBooks(List<BookDTO> bookDTOs) {
        List<Book> books = bookDTOs.stream()
                .map(BookMapper.MAPPER::toEntity)
                .collect(Collectors.toList());
        List<Book> savedBooks = bookRepository.saveAll(books);
        return savedBooks.stream()
                .map(BookMapper.MAPPER::toDTO)
                .collect(Collectors.toList());
    }

    public BookDTO borrowBook(Long userId, Long bookId) {
        if( bookId == null || userId == null) {
            throw new  IllegalArgumentException("id must be not null");
        }
            Optional<Transaction> transactions = transactionRepository.findByUserIdAndBookIdAndIsActiveTrue(userId, bookId);
        if(transactions.isPresent()) {
            throw new IllegalArgumentException("you already have an active transaction for this book");
        }

        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if(bookOptional.isEmpty()) {
            throw new IllegalArgumentException("book not found");
        }
        Book book = bookOptional.get();
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies of the book are currently available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        transactionService.addTransaction(userId, bookId, ActionType.BORROW);
        return BookMapper.MAPPER.toDTO(book);
    }

    public BookDTO returnBook(Long borrowId) {
        var transaction = transactionService.hasActiveTransaction(borrowId)
                .orElseThrow(() -> new EntityNotFoundException("No active transaction found for ID: " + borrowId));

        // Get the book
        Book book = bookRepository.findById(transaction.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));


        transactionService.updateTransaction(borrowId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return BookMapper.MAPPER.toDTO(book);
    }


    public BookDTO getBookById(Long id) {
        return bookRepository.getBookById(id);
    }

    public List<BookDTO> getBookByTitle(String title) {
        List<Book> books = bookRepository.getByTitle(title);
        return BookMapper.MAPPER.toDtoList(books);
    }

    public void deleteBookById(Long Id) {
        bookRepository.deleteById(Id);
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return BookMapper.MAPPER.toDTO(books);
    }

    //todo закладки
}