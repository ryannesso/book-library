package com.library.controller;


import com.library.config.JwtService;
import com.library.dto.UserDTO;
import com.library.dto.request.userRequest.LoginRequest;
import com.library.dto.request.userRequest.RegisterRequest;
import com.library.entity.User;
import com.library.mappers.UserMapper;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserEntityByName(loginRequest.name());
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.badRequest().body("wrong password");
        } else {
            String jwt = jwtService.generateToken(user);
            return ResponseEntity.ok(jwt);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = userMapper.registerRequestToUserDTO(registerRequest);
        userService.addUser(userDTO);
        return ResponseEntity.ok("register ok!");
    }
}

