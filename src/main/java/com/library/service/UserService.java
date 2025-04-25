package com.library.service;


import com.library.dto.UserDTO;
import com.library.mappers.UserMapper;
import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;



    public UserDTO addUser(@RequestBody UserDTO userDTO){
        System.out.println("name: " + userDTO.name());
        User user = userMapper.toEntity(userDTO);
        System.out.println("name: " + user.getName());
        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = userMapper.toDTO(savedUser);
        return savedUserDTO;
        /* TODO addrole for users (default role: USER, admin role: ADMIN) */
    }

    public UserDTO getUserByName(String name) {
        if(userRepository.findByName(name).isPresent()){
            return userMapper.toDTO(userRepository.findByName(name).get());
        }
        return null;
    }

}