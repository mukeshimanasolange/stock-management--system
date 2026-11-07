package com.stockmanagement.service;

import com.stockmanagement.dto.*;
import com.stockmanagement.entity.Supplier;
import com.stockmanagement.exception.ResourceNotFoundException;
import com.stockmanagement.repository.SupplierRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {
    private final SupplierRepository suppliers;
    public SupplierService(SupplierRepository suppliers) { this.suppliers = suppliers; }
    public Page<SupplierDto> list(String search, Pageable pageable) {
        var page = search == null || search.isBlank() ? suppliers.findAll(pageable)
                : suppliers.findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(search.trim(), search.trim(), pageable);
        return page.map(SupplierDto::from);
    }
    public SupplierDto get(Long id) {
        return SupplierDto.from(suppliers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id)));
    }
    @Transactional public SupplierDto create(SupplierRequest request) {
        Supplier s = new Supplier(); apply(s, request); return SupplierDto.from(suppliers.save(s));
    }
    @Transactional public SupplierDto update(Long id, SupplierRequest request) {
        Supplier s = suppliers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        apply(s, request); return SupplierDto.from(s);
    }
    @Transactional public void deactivate(Long id) {
        Supplier s = suppliers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        s.setActive(false);
    }
    private void apply(Supplier s, SupplierRequest r) {
        s.setName(r.name().trim()); s.setPhone(r.phone()); s.setEmail(r.email());
        s.setAddress(r.address()); s.setCompanyName(r.companyName());
        if (r.active() != null) s.setActive(r.active());
    }
}
