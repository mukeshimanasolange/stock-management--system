package com.stockmanagement.controller;

import com.stockmanagement.dto.ProductDto;
import com.stockmanagement.dto.StockDtos.TransactionDto;
import com.stockmanagement.entity.Product;
import com.stockmanagement.entity.TransactionType;
import com.stockmanagement.repository.ProductRepository;
import com.stockmanagement.service.StockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
public class ReportController {
    private final ProductRepository products;
    private final StockService stock;
    public ReportController(ProductRepository products, StockService stock) { this.products = products; this.stock = stock; }
    @GetMapping("/stock")
    @Transactional(readOnly = true)
    public List<ProductDto> stockReport(@RequestParam(required = false) String search) {
        return products.findAll().stream().filter(Product::isActive)
                .filter(p -> search == null || search.isBlank() || p.getName().toLowerCase().contains(search.toLowerCase())
                        || p.getCode().toLowerCase().contains(search.toLowerCase()))
                .map(ProductDto::from).toList();
    }
    @GetMapping("/incoming")
    public List<TransactionDto> incoming(@RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to) {
        return stock.all(from, to, null).stream().filter(t -> t.type() == TransactionType.INCOMING).toList();
    }
    @GetMapping("/outgoing")
    public List<TransactionDto> outgoing(@RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to) {
        return stock.all(from, to, null).stream().filter(t -> t.type() == TransactionType.OUTGOING).toList();
    }
    @GetMapping("/low-stock") public List<ProductDto> lowStock() { return stock.lowStock(); }
}
