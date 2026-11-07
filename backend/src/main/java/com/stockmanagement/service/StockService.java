package com.stockmanagement.service;

import com.stockmanagement.dto.StockDtos.*;
import com.stockmanagement.dto.ProductDto;
import com.stockmanagement.entity.*;
import com.stockmanagement.exception.BadRequestException;
import com.stockmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class StockService {
    private final ProductRepository products;
    private final StockTransactionRepository transactions;
    private final EntityLookup lookup;
    public StockService(ProductRepository products, StockTransactionRepository transactions, EntityLookup lookup) {
        this.products = products; this.transactions = transactions; this.lookup = lookup;
    }
    @Transactional
    public TransactionDto incoming(IncomingRequest request, String actor) {
        Product product = lookup.lockedProduct(request.productId());
        Supplier supplier = lookup.supplier(request.supplierId());
        if (!product.isActive() || !supplier.isActive()) throw new BadRequestException("Product and supplier must be active");
        product.setQuantity(Math.addExact(product.getQuantity(), request.quantity()));
        return TransactionDto.from(saveTransaction(product, supplier, lookup.userByEmail(actor), TransactionType.INCOMING,
                request.quantity(), null, request.notes(), request.transactionDate()));
    }
    @Transactional
    public TransactionDto outgoing(OutgoingRequest request, String actor) {
        Product product = lookup.lockedProduct(request.productId());
        if (!product.isActive()) throw new BadRequestException("Product is inactive");
        if (product.getQuantity() < request.quantity()) throw new BadRequestException("Insufficient stock available");
        product.setQuantity(product.getQuantity() - request.quantity());
        return TransactionDto.from(saveTransaction(product, null, lookup.userByEmail(actor), TransactionType.OUTGOING,
                request.quantity(), request.recipient(), request.notes(), request.transactionDate()));
    }
    private StockTransaction saveTransaction(Product product, Supplier supplier, AppUser user, TransactionType type,
                                             int quantity, String recipient, String notes, Instant date) {
        StockTransaction t = new StockTransaction();
        t.setProduct(product); t.setSupplier(supplier); t.setPerformedBy(user); t.setType(type); t.setQuantity(quantity);
        t.setRecipient(recipient); t.setNotes(notes); t.setTransactionDate(date == null ? Instant.now() : date);
        return transactions.save(t);
    }
    @Transactional(readOnly = true)
    public List<TransactionDto> all(Instant from, Instant to, Long productId) {
        return transactions.findByTransactionDateBetweenOrderByTransactionDateDesc(
                        from == null ? Instant.EPOCH : from, to == null ? Instant.now() : to).stream()
                .filter(t -> productId == null || t.getProduct().getId().equals(productId))
                .map(TransactionDto::from).toList();
    }
    @Transactional(readOnly = true)
    public List<ProductDto> lowStock() {
        return products.findAll().stream().filter(Product::isActive)
                .filter(p -> p.getQuantity() <= p.getMinimumStock())
                .map(com.stockmanagement.dto.ProductDto::from).toList();
    }
}
