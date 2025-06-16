package com.library.dto.request.userRequest;

public record LoginRequest (
        String email,
        String password
) {
}
