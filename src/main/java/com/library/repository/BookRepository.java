package com.library.repository;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    public Optional<Book> findByTitle(String title);
}