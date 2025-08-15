package com.library.repository;

import com.library.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findByUserIdAndBookIdAndIsActiveTrue(Long userId, Long bookId);
    public Optional<Transaction> findById(Long Id);
    public List<Transaction> findAllBy();

    @Query("select count(t) from Transaction t where t.isActive = true")
    int countActiveTransactionsByStatus();
    //todo change to GET
}