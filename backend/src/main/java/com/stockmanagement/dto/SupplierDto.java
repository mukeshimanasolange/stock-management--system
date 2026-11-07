package com.stockmanagement.dto;

import com.stockmanagement.entity.Supplier;

public record SupplierDto(Long id, String name, String phone, String email, String address, String companyName, boolean active) {
    public static SupplierDto from(Supplier s) {
        return new SupplierDto(s.getId(), s.getName(), s.getPhone(), s.getEmail(), s.getAddress(), s.getCompanyName(), s.isActive());
    }
}
