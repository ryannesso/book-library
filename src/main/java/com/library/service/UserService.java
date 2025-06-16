package com.library.service;


import com.library.dto.UserDTO;
import com.library.entity.enums.ERole;
import com.library.mappers.UserMapper;
import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;


    //todo rename to register or registerUser
    public UserDTO addUser(UserDTO userDTO){
        User user = userMapper.toEntity(userDTO);
        if (user.getRole() == null) {
            user.setRole(ERole.USER);
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(userDTO.password());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = userMapper.toDTO(savedUser);
        return savedUserDTO;
    }

    public List<UserDTO> getUserByName(String name) {
        List<User> userList = userRepository.findByName(name);
        if(userList.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        return userMapper.toDTO(userList);
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



}