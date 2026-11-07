package com.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "stock_requests", indexes = @Index(name = "idx_requests_status_date", columnList = "status,requested_at"))
public class StockRequest extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private AppUser requestedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private AppUser reviewedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;
    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;
    @Column(name = "reviewed_at")
    private Instant reviewedAt;
    @Column(name = "issued_at")
    private Instant issuedAt;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockRequestItem> items = new ArrayList<>();
}
