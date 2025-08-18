package com.library.service;

import com.library.entity.Book;
import com.library.entity.Comment;
import com.library.repository.BookRepository;
import com.library.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    public CommentService(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    public List<Comment> getCommentsByBookId(Long bookId) {
        return commentRepository.findByBookIdOrderByCreatedAtAsc(bookId);
    }

    public Comment addComment(Long bookId, String username, String text) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUsername(username);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
