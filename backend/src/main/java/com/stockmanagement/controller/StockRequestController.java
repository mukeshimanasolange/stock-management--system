package com.stockmanagement.controller;

import com.stockmanagement.dto.RequestDtos.*;
import com.stockmanagement.service.StockRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class StockRequestController {
    private final StockRequestService service;
    public StockRequestController(StockRequestService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@Valid @RequestBody CreateRequest request, Authentication auth) {
        return service.create(request, auth.getName());
    }
    @GetMapping
    public List<RequestDto> list(Authentication auth) {
        boolean canReview = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STOREKEEPER"));
        return service.list(auth.getName(), canReview);
    }
    @PutMapping("/{id}/approve") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public RequestDto approve(@PathVariable Long id, @Valid @RequestBody(required = false) ReviewRequest body, Authentication auth) {
        return service.approve(id, auth.getName(), body == null ? null : body.notes());
    }
    @PutMapping("/{id}/reject") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public RequestDto reject(@PathVariable Long id, @Valid @RequestBody(required = false) ReviewRequest body, Authentication auth) {
        return service.reject(id, auth.getName(), body == null ? null : body.notes());
    }
    @PutMapping("/{id}/issue") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public RequestDto issue(@PathVariable Long id, Authentication auth) { return service.issue(id, auth.getName()); }
}
