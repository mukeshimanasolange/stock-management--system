package com.stockmanagement.repository;

import com.stockmanagement.entity.StockTransaction;
import com.stockmanagement.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findTop10ByOrderByTransactionDateDesc();
    List<StockTransaction> findByTransactionDateBetweenOrderByTransactionDateDesc(Instant from, Instant to);
    List<StockTransaction> findByTypeAndTransactionDateBetweenOrderByTransactionDateDesc(TransactionType type, Instant from, Instant to);
    long countByType(TransactionType type);
    @Query("select coalesce(sum(t.quantity), 0) from StockTransaction t where t.type = :type")
    long sumQuantityByType(@Param("type") TransactionType type);
}
