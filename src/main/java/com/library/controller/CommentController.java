package com.library.controller;

import com.library.dto.request.CommentRequest;
import com.library.entity.Book;
import com.library.entity.Comment;
import com.library.repository.BookRepository;
import com.library.repository.CommentRepository;
import com.library.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final CommentService commentService;

    public CommentController(CommentRepository commentRepository, BookRepository bookRepository, CommentService commentService) {
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    @GetMapping("/{bookId}")
    public List<Comment> getComments(@PathVariable Long bookId) {
        return commentRepository.findByBookIdOrderByCreatedAtAsc(bookId);
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<Comment> addComment(@PathVariable Long bookId,
                                              @RequestBody CommentRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Comment saved = commentService.addComment(bookId, userDetails.getUsername(), request.getText());
        return ResponseEntity.ok(saved);
    }

}
