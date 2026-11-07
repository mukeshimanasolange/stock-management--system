package com.stockmanagement.dto;

import com.stockmanagement.entity.Product;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(Long id, String name, String code, String description, BigDecimal unitPrice,
                         int quantity, int minimumStock, boolean active, Long categoryId,
                         String categoryName, Instant registeredAt) {
    public static ProductDto from(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getCode(), p.getDescription(), p.getUnitPrice(),
                p.getQuantity(), p.getMinimumStock(), p.isActive(), p.getCategory().getId(),
                p.getCategory().getName(), p.getCreatedAt());
    }
}
