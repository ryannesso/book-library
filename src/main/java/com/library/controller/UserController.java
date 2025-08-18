package com.library.controller;

import com.library.config.JwtService;
import com.library.dto.BookDTO;
import com.library.dto.UserDTO;
import com.library.entity.Transaction;
import com.library.entity.User;
import com.library.mappers.BookMapper;
import com.library.mappers.UserMapper;
import com.library.service.BookService;
import com.library.service.TransactionService;
import com.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BookService bookService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TransactionService transactionService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserController(UserService userService, BookService bookService, JwtService jwtService,
                          UserDetailsService userDetailsService, TransactionService transactionService,
                          UserMapper userMapper, BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.transactionService = transactionService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create_user")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        User user = userService.addUser(userDTO);
        return userMapper.toDTO(user);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO updatedUserDTO
    ) {

        User updatedUser = userMapper.toEntity(updatedUserDTO);
        userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@CookieValue(name = "jwt", required = false) String jwt) {
        if(jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Now authorized");
        }
        String username = jwtService.extractName(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(!jwtService.isTokenValid(jwt, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        UserDTO userDTO = userMapper.toDTO(userService.getUserByEmail(username));
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/my_books")
    public ResponseEntity<?> getUsersBooks(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized");
        }

        Long userId;
        try {
            userId = jwtService.extractUserId(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Id");
        }

        // Получаем все транзакции пользователя
        List<Transaction> usersTransactions = transactionService.getAllTransactions().stream()
                .filter(t -> t.getUserId().equals(userId) && t.isActive())
                .toList();

        // Получаем список всех книг
        List<BookDTO> allBooks = bookMapper.toDTO(bookService.getAllBooks());

        // Получаем список id книг, взятых пользователем
        Set<Long> userBookIds = usersTransactions.stream()
                .map(Transaction::getBookId)
                .collect(Collectors.toSet());
        // Фильтруем книги по id
        List<BookDTO> userBooks = allBooks.stream()
                .filter(book -> userBookIds.contains(book.id()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userBooks);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUsers(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized");
        }
        List<UserDTO> userDTOs = userMapper.toDTO(userService.getAllUsers());
        return ResponseEntity.ok(userDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<?> getUserStats(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized");
        }
        return ResponseEntity.ok(userService.getAdminStats());
    }


}
