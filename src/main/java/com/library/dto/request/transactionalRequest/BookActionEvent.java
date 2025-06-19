package com.library.dto.request.transactionalRequest;

public record BookActionEvent(
        Long userId,
        Long bookId,
        String actionType
) {
}
