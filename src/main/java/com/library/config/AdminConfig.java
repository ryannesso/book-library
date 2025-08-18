package com.library.config;


import com.library.entity.User;
import com.library.entity.enums.ERole;
import com.library.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @PostConstruct
    public void initAdmin() {
        System.out.println(">>> Init admin with email: " + adminEmail);
        if(userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(ERole.ADMIN);
            admin.setCredits(99999);
            admin.setBorrowBooks(0);
            userRepository.save(admin);
        }
    }

}
