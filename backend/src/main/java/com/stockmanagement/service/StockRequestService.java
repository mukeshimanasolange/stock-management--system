package com.stockmanagement.service;

import com.stockmanagement.dto.RequestDtos.*;
import com.stockmanagement.entity.*;
import com.stockmanagement.exception.*;
import com.stockmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class StockRequestService {
    private final StockRequestRepository requests;
    private final ProductRepository products;
    private final StockTransactionRepository transactions;
    private final EntityLookup lookup;
    public StockRequestService(StockRequestRepository requests, ProductRepository products,
            StockTransactionRepository transactions, EntityLookup lookup) {
        this.requests = requests; this.products = products; this.transactions = transactions; this.lookup = lookup;
    }

    @Transactional
    public RequestDto create(CreateRequest request, String actor) {
        if (request.items().stream().map(ItemRequest::productId).distinct().count() != request.items().size())
            throw new BadRequestException("Each product may appear only once in a request");
        StockRequest entity = new StockRequest();
        entity.setRequestedBy(lookup.userByEmail(actor));
        entity.setRequestedAt(Instant.now());
        for (ItemRequest requestedItem : request.items()) {
            Product product = lookup.product(requestedItem.productId());
            if (!product.isActive()) throw new BadRequestException("Inactive products cannot be requested");
            StockRequestItem item = new StockRequestItem();
            item.setRequest(entity); item.setProduct(product); item.setQuantity(requestedItem.quantity());
            entity.getItems().add(item);
        }
        return RequestDto.from(requests.save(entity));
    }

    @Transactional(readOnly = true)
    public List<RequestDto> list(String actor, boolean canReview) {
        var result = canReview ? requests.findAllByOrderByRequestedAtDesc()
                : requests.findByRequestedByIdOrderByRequestedAtDesc(lookup.userByEmail(actor).getId());
        return result.stream().map(RequestDto::from).toList();
    }

    @Transactional
    public RequestDto approve(Long id, String actor, String notes) {
        StockRequest request = lock(id);
        requirePending(request);
        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedBy(lookup.userByEmail(actor));
        request.setReviewedAt(Instant.now());
        request.setReviewNotes(notes);
        return RequestDto.from(request);
    }

    @Transactional
    public RequestDto reject(Long id, String actor, String notes) {
        StockRequest request = lock(id);
        requirePending(request);
        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedBy(lookup.userByEmail(actor));
        request.setReviewedAt(Instant.now());
        request.setReviewNotes(notes);
        return RequestDto.from(request);
    }

    @Transactional
    public RequestDto issue(Long id, String actor) {
        StockRequest request = lock(id);
        if (request.getStatus() != RequestStatus.APPROVED) throw new BadRequestException("Only approved requests can be issued");
        List<Long> productIds = request.getItems().stream().map(item -> item.getProduct().getId()).sorted().toList();
        var lockedProducts = productIds.stream().map(lookup::lockedProduct).toList();
        for (Product p : lockedProducts) {
            int requested = request.getItems().stream().filter(i -> i.getProduct().getId().equals(p.getId()))
                    .mapToInt(StockRequestItem::getQuantity).findFirst().orElseThrow();
            if (!p.isActive() || p.getQuantity() < requested)
                throw new BadRequestException("Insufficient active stock for product " + p.getCode());
        }
        AppUser issuer = lookup.userByEmail(actor);
        for (Product p : lockedProducts) {
            int quantity = request.getItems().stream().filter(i -> i.getProduct().getId().equals(p.getId()))
                    .mapToInt(StockRequestItem::getQuantity).findFirst().orElseThrow();
            p.setQuantity(p.getQuantity() - quantity);
            StockTransaction transaction = new StockTransaction();
            transaction.setProduct(p); transaction.setPerformedBy(issuer); transaction.setType(TransactionType.OUTGOING);
            transaction.setQuantity(quantity); transaction.setRecipient(request.getRequestedBy().getFullName());
            transaction.setNotes("Issued for stock request #" + request.getId());
            transaction.setTransactionDate(Instant.now());
            transactions.save(transaction);
        }
        request.setStatus(RequestStatus.ISSUED);
        request.setIssuedAt(Instant.now());
        request.setReviewedBy(issuer);
        if (request.getReviewedAt() == null) request.setReviewedAt(Instant.now());
        return RequestDto.from(request);
    }

    private StockRequest lock(Long id) {
        return requests.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Stock request not found: " + id));
    }
    private void requirePending(StockRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) throw new BadRequestException("Only pending requests can be reviewed");
    }
}
