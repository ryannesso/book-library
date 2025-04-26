package com.library.repository;

import com.library.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findByUserIdAndBookId(Long userId, Long bookId);
    public Optional<Transaction> findById(Long Id);
}