package com.library.service;

//import com.library.BooksDTOMapper;
import com.library.entity.enums.ActionType;
import com.library.mappers.BookMapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TransactionService transactionService;


    public BookDTO addBook(BookDTO bookDTO) {
        if(bookRepository.findByTitle(bookDTO.title()).isPresent()){
            throw new RuntimeException("Book already exists");
        }
        Book book = bookMapper.MAPPER.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        BookDTO savedBookDTO = bookMapper.MAPPER.toDTO(savedBook);
        return savedBookDTO;
    }

    public BookDTO borrowBook(Long userId, Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Book book = bookOptional.get();
        transactionService.addTransaction(userId, bookId, ActionType.BORROW);
        return null;
    }

    public BookDTO returnBook(Long borrowId) {
        var transaction = transactionService.hasActiveTransaction(borrowId)
                .orElseThrow(() -> new EntityNotFoundException("No active transaction found for ID: " + borrowId));

        // Get the book
        Book book = bookRepository.findById(transaction.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));


        transactionService.updateTransaction(borrowId);
        return null;
    }


    public List<BookDTO> findBookById(Long bookId) {
        return bookRepository.findBookById(bookId);
    }

    /* TODO create borrow and return methods */

}