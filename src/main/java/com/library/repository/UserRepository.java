package com.library.repository;

import com.library.dto.UserDTO;
import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    public List<User> findByName(String name);
    //todo change to GET

}
