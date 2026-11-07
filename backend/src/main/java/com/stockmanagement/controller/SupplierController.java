package com.stockmanagement.controller;

import com.stockmanagement.dto.SupplierDto;
import com.stockmanagement.dto.SupplierRequest;
import com.stockmanagement.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService service) { this.service = service; }
    @GetMapping public Page<SupplierDto> list(@RequestParam(required = false) String search,
             @PageableDefault(size = 20, sort = "name") Pageable pageable) { return service.list(search, pageable); }
    @GetMapping("/{id}") public SupplierDto get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public SupplierDto create(@Valid @RequestBody SupplierRequest request) { return service.create(request); }
    @PutMapping("/{id}") public SupplierDto update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deactivate(id); }
}
