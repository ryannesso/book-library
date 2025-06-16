package com.library.controller;


import com.library.config.JwtService;
import com.library.dto.UserDTO;
import com.library.dto.request.userRequest.LoginRequest;
import com.library.dto.request.userRequest.RegisterRequest;
import com.library.entity.User;
import com.library.mappers.UserMapper;
import com.library.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
        User user = userService.getUserByEmail(loginRequest.email());
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.badRequest().body("wrong password");
        } else {
            String jwt = jwtService.generateToken(user);
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Lax")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body("Login successful");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = userMapper.registerRequestToUserDTO(registerRequest);
        userService.addUser(userDTO);
        return ResponseEntity.ok("register ok!");
    }
}

