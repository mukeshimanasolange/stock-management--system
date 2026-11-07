package com.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "stock_transactions", indexes = @Index(name = "idx_transactions_date_type", columnList = "transaction_date,type"))
public class StockTransaction extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private AppUser performedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private TransactionType type;
    @Column(nullable = false)
    private int quantity;
    @Column(length = 190)
    private String recipient;
    @Column(length = 1000)
    private String notes;
    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;
}
