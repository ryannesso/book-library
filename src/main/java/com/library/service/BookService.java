package com.library.service;

import com.library.entity.enums.ActionType;
import com.library.mappers.BookMapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private TransactionService transactionService;


    public BookDTO addBook(BookDTO bookDTO) {
//        if(!bookRepository.findByTitle(bookDTO.title()).isEmpty()){
//            throw new RuntimeException("Book already exists");
//        }
// без проверки на название потому что может быть несколько книг с одинаковым названием

        Book book = BookMapper.MAPPER.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        BookDTO savedBookDTO = BookMapper.MAPPER.toDTO(savedBook);
        return savedBookDTO;
    }

    public BookDTO borrowBook(Long userId, Long bookId) {
        if( bookId == null && userId == null) {
            throw new  IllegalArgumentException("id must be not null");
        }
        Optional<Book> bookOptional = bookRepository.findById(bookId);
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