package com.stockmanagement.dto;

import com.stockmanagement.entity.StockTransaction;
import com.stockmanagement.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class StockDtos {
    private StockDtos() {}
    public record IncomingRequest(@NotNull Long productId, @NotNull Long supplierId, @Positive int quantity,
                                  Instant transactionDate, @Size(max = 1000) String notes) {}
    public record OutgoingRequest(@NotNull Long productId, @Positive int quantity, @NotNull @Size(max = 190) String recipient,
                                  Instant transactionDate, @Size(max = 1000) String notes) {}
    public record TransactionDto(Long id, Long productId, String productName, String productCode, Long supplierId,
                                 String supplierName, Long performedById, String performedBy, TransactionType type,
                                 int quantity, String recipient, String notes, Instant transactionDate) {
        public static TransactionDto from(StockTransaction t) {
            return new TransactionDto(t.getId(), t.getProduct().getId(), t.getProduct().getName(),
                    t.getProduct().getCode(), t.getSupplier() == null ? null : t.getSupplier().getId(),
                    t.getSupplier() == null ? null : t.getSupplier().getName(), t.getPerformedBy().getId(),
                    t.getPerformedBy().getFullName(), t.getType(), t.getQuantity(), t.getRecipient(),
                    t.getNotes(), t.getTransactionDate());
        }
    }
}
