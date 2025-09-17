package com.library.repository;

import com.library.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findByUserIdAndBookIdAndIsActiveTrue(Long userId, Long bookId);
    public List<Transaction> findByUserId(Long Id);
    public List<Transaction> findAllBy();
    List<Transaction> findByBorrowDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByBorrowDateBetweenAndUserId(LocalDateTime startDate, LocalDateTime endDate, Long userId);
    @Query("select count(t) from Transaction t where t.isActive = true")
    int countActiveTransactionsByStatus();
    //todo change to GET
}