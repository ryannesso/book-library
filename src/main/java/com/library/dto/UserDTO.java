package com.library.dto;


import com.library.entity.enums.ERole;

import java.util.HashSet;
import java.util.Set;

public record UserDTO(
        Long id,
        String name,
        String email,
        String password,
        String role,
        int credits ){
}
