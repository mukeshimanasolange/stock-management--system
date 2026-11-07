package com.stockmanagement.repository;

import com.stockmanagement.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(String name, String companyName, Pageable pageable);
}
