package com.stockmanagement.controller;

import com.stockmanagement.dto.StockDtos.TransactionDto;
import com.stockmanagement.entity.TransactionType;
import com.stockmanagement.repository.*;
import com.stockmanagement.service.StockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
public class DashboardController {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final SupplierRepository suppliers;
    private final UserRepository users;
    private final StockTransactionRepository transactions;
    private final StockRequestRepository requests;
    private final StockService stock;
    public DashboardController(ProductRepository products, CategoryRepository categories, SupplierRepository suppliers,
            UserRepository users, StockTransactionRepository transactions, StockRequestRepository requests, StockService stock) {
        this.products = products; this.categories = categories; this.suppliers = suppliers; this.users = users;
        this.transactions = transactions; this.requests = requests; this.stock = stock;
    }
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return Map.of("totalProducts", products.countByActiveTrue(), "totalCategories", categories.count(),
                "totalSuppliers", suppliers.count(), "totalUsers", users.count(),
                "availableStock", products.sumActiveQuantities(),
                "incomingStock", transactions.sumQuantityByType(TransactionType.INCOMING),
                "outgoingStock", transactions.sumQuantityByType(TransactionType.OUTGOING),
                "pendingRequests", requests.countByStatus(com.stockmanagement.entity.RequestStatus.PENDING),
                "lowStockProducts", products.countLowStock());
    }
    @GetMapping("/stock-movements")
    public List<Map<String,Object>> movements(@RequestParam(required = false) Instant from,
                                              @RequestParam(required = false) Instant to) {
        List<TransactionDto> rows = stock.all(from, to, null);
        Map<String, Map<String, Long>> grouped = new TreeMap<>();
        for (TransactionDto row : rows) {
            String day = row.transactionDate().toString().substring(0, 10);
            Map<String, Long> totals = grouped.computeIfAbsent(day, key -> {
                Map<String, Long> values = new HashMap<>(); values.put("incoming", 0L); values.put("outgoing", 0L); return values;
            });
            String key = row.type() == TransactionType.INCOMING ? "incoming" : "outgoing";
            totals.put(key, totals.get(key) + row.quantity());
        }
        return grouped.entrySet().stream().map(entry -> Map.<String,Object>of("date", entry.getKey(),
                "incoming", entry.getValue().get("incoming"), "outgoing", entry.getValue().get("outgoing"))).toList();
    }
    @GetMapping("/recent-transactions") @Transactional(readOnly = true)
    public List<TransactionDto> recent() {
        return transactions.findTop10ByOrderByTransactionDateDesc().stream().map(TransactionDto::from).toList();
    }
    @GetMapping("/low-stock") public Object lowStock() { return stock.lowStock(); }
}
