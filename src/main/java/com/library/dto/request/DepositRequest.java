package com.library.dto.request;

public record DepositRequest(int amount, String currency, String token) {
}
