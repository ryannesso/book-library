package com.library.dto;

import lombok.Data;

import java.util.List;
public record BookDTO(
        String title,
        String author,
        boolean status,
        String description,
        int availableCopies
) {

}
