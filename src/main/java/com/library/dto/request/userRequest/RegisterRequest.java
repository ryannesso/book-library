package com.library.dto.request.userRequest;

public record RegisterRequest(
        Long id,
        String name,
        String email,
        String password,
        String role
) {

}
