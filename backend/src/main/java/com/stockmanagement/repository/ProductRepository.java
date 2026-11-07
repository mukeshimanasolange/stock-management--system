package com.stockmanagement.repository;

import com.stockmanagement.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCodeIgnoreCase(String code);
    @Query("""
            select p from Product p
            where (:includeInactive = true or p.active = true)
              and (:search is null or :search = ''
                   or lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.code) like lower(concat('%', :search, '%')))
            """)
    Page<Product> search(@Param("search") String search, @Param("includeInactive") boolean includeInactive, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
    long countByActiveTrue();
    @Query("select coalesce(sum(p.quantity), 0) from Product p where p.active = true")
    long sumActiveQuantities();
    @Query("select count(p) from Product p where p.active = true and p.quantity <= p.minimumStock")
    long countLowStock();
}
