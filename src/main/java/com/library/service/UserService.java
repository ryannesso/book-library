package com.library.service;


import com.library.dto.UserDTO;
import com.library.entity.Book;
import com.library.entity.enums.ERole;
import com.library.mappers.UserMapper;
import com.library.entity.User;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    //todo rename to register or registerUser
    public User addUser(UserDTO userDTO){
        User user = userMapper.toEntity(userDTO);
        if (user.getRole() == null) {
            user.setRole(ERole.USER);
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(userDTO.password());
        user.setPassword(encodedPassword);
        user.setCredits(1000);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user){
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        if(user.getPassword() == null){
            System.out.println("password will not to change");
        }
        else {
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        updatedUser.setCredits(user.getCredits());
        return userRepository.save(updatedUser);
    }

    public List<User> getUserByName(String name) {
        List<User> userList = userRepository.findByName(name);
        if(userList.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        return userList;
    }

    public User getUserByEmail(String email) {
        if(userRepository.findByEmail(email).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userRepository.findByEmail(email).get();
    }

    public User getUserEntityByName(String name) {
        List<User> userList = userRepository.findByName(name);
        if(userList.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        return userList.get(0);
    }

    public User getUserById(Long id) {
        if(userRepository.findById(id).isPresent()){
            return userRepository.findById(id).get();
        }
        return null;
    }

    public int getCredits(Long userId) {
        Optional<User> OpUser = userRepository.findById(userId);
        User user = OpUser.orElseThrow(() -> new EntityNotFoundException("user not found"));
        return user.getCredits();
    }

    public void subtractCredits(Long userId, int price) {
        Optional<User> OpUser = userRepository.findById(userId);
        User user = OpUser.orElseThrow(() -> new EntityNotFoundException("user not found"));
        user.setCredits(user.getCredits() - price);
        userRepository.save(user);
    }

    public void addCredits(Long userId, int price) {
        Optional<User> OpUser = userRepository.findById(userId);
        User user = OpUser.orElseThrow(() -> new EntityNotFoundException("user not found"));
        user.setCredits(user.getCredits() + price);
        userRepository.save(user);
    }

    public void addBorrowCount(Long userId) {
        Optional<User> OpUser = userRepository.findById(userId);
        User user = OpUser.orElseThrow(() -> new EntityNotFoundException("user not found"));
        user.setBorrowBooks(user.getBorrowBooks() + 1);
        userRepository.save(user);
    }

    public void subtractBorrowCount(Long userId) {
        Optional<User> OpUser = userRepository.findById(userId);
        User user = OpUser.orElseThrow(() -> new EntityNotFoundException("user not found"));
        user.setBorrowBooks(user.getBorrowBooks() - 1);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Map<String, Integer> getAdminStats() {
        int bookCount = bookRepository.findAll().size();
        int userCount = userRepository.findAll().size();
        int borrowCount = transactionRepository.countActiveTransactionsByStatus();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("bookCount", bookCount);
        stats.put("userCount", userCount);
        stats.put("borrowCount", borrowCount);
        return stats;
    }

}