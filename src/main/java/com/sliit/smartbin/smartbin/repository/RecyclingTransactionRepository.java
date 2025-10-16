package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecyclingTransactionRepository extends JpaRepository<RecyclingTransaction, Long> {
    List<RecyclingTransaction> findByUserOrderByCreatedAtDesc(User user);
    List<RecyclingTransaction> findByUserAndStatus(User user, RecyclingTransaction.TransactionStatus status);
}

