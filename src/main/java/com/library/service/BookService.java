package com.library.service;

import com.library.dto.request.transactionalRequest.BookActionEvent;
import com.library.entity.Transaction;
import com.library.mappers.BookMapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {


    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

     private  final BookRepository bookRepository;


    public Book addBooks(Book book) {
        book.setStatus(true);
        return bookRepository.save(book);
    }

//    public void borrowBook(Long userId, Long bookId) {
//        if( bookId == null || userId == null) {
//            throw new  IllegalArgumentException("id must be not null");
//        }
//        BookActionEvent event = new BookActionEvent(userId, bookId, "BORROW");
//        kafkaProducer.sendBookAction(event);
//    }
//
//    public void returnBook(Long borrowId) {
//        Optional<Transaction> OpTransaction = transactionRepository.findById(borrowId);
//        Transaction transaction = OpTransaction.orElseThrow(() -> new EntityNotFoundException("transaction not found"));
//        Long userId = transaction.getUserId();
//        Long bookId = transaction.getBookId();
//        BookActionEvent event = new BookActionEvent(userId, bookId, "RETURN");
//        kafkaProducer.sendBookAction(event);
//    }


    public Book getBookById(Long id) {
        return bookRepository.getBookById(id);
    }

    public List<Book> getBookByTitle(String title) {
        return bookRepository.getByTitle(title);
    }

    public void deleteBookById(Long Id) {
        bookRepository.deleteById(Id);
    }

    public Book updateBook(Long id, Book book) {
//        Book book = bookMapper.toEntity(updatedBookDTO);
        Book updatedBook = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        updatedBook.setTitle(book.getTitle());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setDescription(book.getDescription());
        updatedBook.setStatus(book.isStatus());
        updatedBook.setAvailableCopies(book.getAvailableCopies());
        updatedBook.setPrice(book.getPrice());
        return bookRepository.save(updatedBook);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public int getPrice(Long bookId) {
        Optional<Book> OpBook = bookRepository.findById(bookId);
        Book book = OpBook.orElseThrow(() -> new EntityNotFoundException("book not found"));
        return book.getPrice();
    }

    public boolean getStatus(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("book not found"));
        return book.isStatus();
    }

}