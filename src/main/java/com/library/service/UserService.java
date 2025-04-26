package com.library.service;


import com.library.dto.UserDTO;
import com.library.entity.enums.ERole;
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
        User user = userMapper.toEntity(userDTO);
        if (user.getRole() == null) {
            user.setRole(ERole.USER);
        }
        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = userMapper.toDTO(savedUser);
        return savedUserDTO;
    }

    public UserDTO getUserByName(String name) {
        if(userRepository.findByName(name).isPresent()){
            return userMapper.toDTO(userRepository.findByName(name).get());
        }
        return null;
    }

    public User getUserById(Long id) {
        if(userRepository.findById(id).isPresent()){
            return userRepository.findById(id).get();
        }
        return null;
    }

}