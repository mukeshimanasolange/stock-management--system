package com.stockmanagement.repository;

import com.stockmanagement.entity.StockRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {
    @EntityGraph(attributePaths = {"requestedBy", "reviewedBy", "items", "items.product"})
    List<StockRequest> findAllByOrderByRequestedAtDesc();
    @EntityGraph(attributePaths = {"requestedBy", "reviewedBy", "items", "items.product"})
    List<StockRequest> findByRequestedByIdOrderByRequestedAtDesc(Long userId);
    @EntityGraph(attributePaths = {"requestedBy", "reviewedBy", "items", "items.product"})
    Optional<StockRequest> findWithDetailsById(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StockRequest r where r.id = :id")
    Optional<StockRequest> findByIdForUpdate(@Param("id") Long id);
    long countByStatus(com.stockmanagement.entity.RequestStatus status);
}
