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


    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

     private  final BookRepository bookRepository;


    public BookDTO addBooks(BookDTO bookDTO) {
        Book book = BookMapper.MAPPER.toEntity(bookDTO);
        book.setStatus(true);
        book = bookRepository.save(book);
        return BookMapper.MAPPER.toDTO(book);
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

    public List<BookDTO> getBookByTitle(String title) {
        List<Book> books = bookRepository.getByTitle(title);
        return BookMapper.MAPPER.toDtoList(books);
    }

    public void deleteBookById(Long Id) {
        bookRepository.deleteById(Id);
    }

    public Book updateBook(Long id, BookDTO updatedBookDTO) {
        Book book = bookRepository.getBookById(id);
        book.setTitle(updatedBookDTO.title());
        book.setAuthor(updatedBookDTO.author());
        book.setDescription(updatedBookDTO.description());
        book.setPrice(updatedBookDTO.price());
        book.setAvailableCopies(updatedBookDTO.availableCopies());
        book.setStatus(updatedBookDTO.status());

        return bookRepository.save(book);
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return BookMapper.MAPPER.toDTO(books);
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