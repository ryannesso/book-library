package com.library.repository;

import com.library.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findByUserIdAndBookIdAndIsActiveTrue(Long userId, Long bookId);
    public Optional<Transaction> findById(Long Id);
    public  Optional<Transaction> findByBookId(Long bookId);
    //todo change to GET
}