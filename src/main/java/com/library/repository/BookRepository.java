package com.library.repository;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    public List<Book> findByTitleContainingIgnoreCase(String title);
    public Book getBookById(Long id);
}