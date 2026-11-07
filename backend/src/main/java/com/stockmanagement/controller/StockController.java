package com.stockmanagement.controller;

import com.stockmanagement.dto.ProductDto;
import com.stockmanagement.dto.StockDtos.*;
import com.stockmanagement.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService service;
    public StockController(StockService service) { this.service = service; }
    @PostMapping("/incoming") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto incoming(@Valid @RequestBody IncomingRequest request, Authentication auth) {
        return service.incoming(request, auth.getName());
    }
    @PostMapping("/outgoing") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto outgoing(@Valid @RequestBody OutgoingRequest request, Authentication auth) {
        return service.outgoing(request, auth.getName());
    }
    @GetMapping("/transactions") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public List<TransactionDto> transactions(@RequestParam(required = false) Instant from,
             @RequestParam(required = false) Instant to, @RequestParam(required = false) Long productId) {
        return service.all(from, to, productId);
    }
    @GetMapping("/low-stock") public List<ProductDto> lowStock() { return service.lowStock(); }
}
