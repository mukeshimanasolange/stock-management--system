package com.stockmanagement.dto;

import com.stockmanagement.entity.RequestStatus;
import com.stockmanagement.entity.StockRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class RequestDtos {
    private RequestDtos() {}
    public record ItemRequest(@NotNull Long productId, @Positive int quantity) {}
    public record CreateRequest(@NotEmpty List<@Valid ItemRequest> items) {}
    public record ReviewRequest(@Size(max = 1000) String notes) {}
    public record ItemDto(Long productId, String productName, String productCode, int quantity) {}
    public record RequestDto(Long id, Long requestedById, String requestedBy, Long reviewedById, String reviewedBy,
                             RequestStatus status, String reviewNotes, Instant requestedAt, Instant reviewedAt,
                             Instant issuedAt, List<ItemDto> items) {
        public static RequestDto from(StockRequest r) {
            return new RequestDto(r.getId(), r.getRequestedBy().getId(), r.getRequestedBy().getFullName(),
                    r.getReviewedBy() == null ? null : r.getReviewedBy().getId(),
                    r.getReviewedBy() == null ? null : r.getReviewedBy().getFullName(), r.getStatus(),
                    r.getReviewNotes(), r.getRequestedAt(), r.getReviewedAt(), r.getIssuedAt(),
                    r.getItems().stream().map(i -> new ItemDto(i.getProduct().getId(), i.getProduct().getName(),
                            i.getProduct().getCode(), i.getQuantity())).toList());
        }
    }
}
