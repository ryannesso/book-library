package com.library.dto.request;

public record BorrowRequest(
        Long Id,
        Long bookId,
        Long userId
) {
}
