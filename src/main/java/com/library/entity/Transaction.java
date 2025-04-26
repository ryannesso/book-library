package com.library.entity;

import com.library.entity.enums.ActionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime BorrowDate;
    private LocalDateTime ReturnDate;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private Long userId;
    private Long bookId;

    private boolean isActive;


}