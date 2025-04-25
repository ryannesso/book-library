package com.library.service;

//import com.library.BooksDTOMapper;
import com.library.mappers.BookMapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;



    public BookDTO addBook(BookDTO bookDTO) {
        if(bookRepository.findByTitle(bookDTO.title()).isPresent()){
            throw new RuntimeException("Book already exists");
        }
        Book book = bookMapper.MAPPER.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        BookDTO savedBookDTO = bookMapper.MAPPER.toDTO(savedBook);
        return savedBookDTO;
    }

    /* TODO create borrow and return methods */

}