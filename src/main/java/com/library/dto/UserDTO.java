package com.library.dto;


import java.util.Set;

public record UserDTO(
        Long id,
        String name,
        String email,
        String password){
}
