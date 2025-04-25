package com.library.controller;

import com.library.dto.UserDTO;
import com.library.mappers.UserMapper;
import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/adduser")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @GetMapping("/getuser")
    public UserDTO getUserByName(@RequestBody UserDTO userDTO) {
        return userService.getUserByName(userDTO.name());
    }

//    @GetMapping("/get-user-rentals")
//    public UserDTO getUserRentalBooks(@RequestBody UserDTO userDTO) {
//        if(userRepository.findByName(userDTO.name()).isPresent()){
//
//        }
//    }
}
