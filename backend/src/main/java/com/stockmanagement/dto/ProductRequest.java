package com.stockmanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 60) String code,
        @Size(max = 10000) String description,
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
        @NotNull Long categoryId,
        @NotNull @PositiveOrZero Integer minimumStock,
        Boolean active) {}
