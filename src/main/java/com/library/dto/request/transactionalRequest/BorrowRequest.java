package com.library.dto.request.transactionalRequest;

public record BorrowRequest(
        Long Id,
        Long bookId,
        Long userId
) {
}
