package com.library.repository;

import com.library.entity.Book;
import com.library.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookId(Long bookId);
    List<Comment> findByBookIdOrderByCreatedAtAsc(Long bookId);}
