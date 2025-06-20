package com.library.dto;

public record BookDTO(
        Long id,
        String title,
        String author,
        boolean status,
        String description,
        int availableCopies,
        int price
) {

}
