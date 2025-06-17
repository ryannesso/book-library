package com.library.controller;

import com.library.config.JwtService;
import com.library.dto.BookDTO;
import com.library.dto.UserDTO;
import com.library.entity.Transaction;
import com.library.mappers.UserMapper;
import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.TransactionService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TransactionService transactionService;



    @PostMapping("/create_user")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
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

        User user = userService.getUserByEmail(username);
        return ResponseEntity.ok(user);
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
                .collect(Collectors.toList());

        // Получаем список всех книг
        List<BookDTO> allBooks = bookService.getAllBooks();

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







//    @GetMapping("/get-user-rentals")
//    public UserDTO getUserRentalBooks(@RequestBody UserDTO userDTO) {
//        if(userRepository.findByName(userDTO.name()).isPresent()){
//
//        }
//    }
}
