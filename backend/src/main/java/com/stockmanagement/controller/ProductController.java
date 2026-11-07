package com.stockmanagement.controller;

import com.stockmanagement.dto.ProductDto;
import com.stockmanagement.dto.ProductRequest;
import com.stockmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }
    @GetMapping
    public Page<ProductDto> list(@RequestParam(required = false) String search,
                                 @PageableDefault(size = 20, sort = "name") Pageable pageable, Authentication auth) {
        boolean includeInactive = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STOREKEEPER"));
        return service.list(search, includeInactive, pageable);
    }
    @GetMapping("/{id}")
    public ProductDto get(@PathVariable Long id, Authentication auth) {
        boolean includeInactive = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STOREKEEPER"));
        return service.get(id, includeInactive);
    }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public ProductDto create(@Valid @RequestBody ProductRequest request) { return service.create(request); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deactivate(id); }
}
