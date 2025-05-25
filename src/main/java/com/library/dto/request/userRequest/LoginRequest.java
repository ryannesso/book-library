package com.library.dto.request.userRequest;

public record LoginRequest (
        String name,
        String password
) {
}
