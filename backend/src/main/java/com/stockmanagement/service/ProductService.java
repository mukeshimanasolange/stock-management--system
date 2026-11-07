package com.stockmanagement.service;

import com.stockmanagement.dto.ProductDto;
import com.stockmanagement.dto.ProductRequest;
import com.stockmanagement.entity.Product;
import com.stockmanagement.exception.ConflictException;
import com.stockmanagement.exception.ResourceNotFoundException;
import com.stockmanagement.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository products;
    private final EntityLookup lookup;
    public ProductService(ProductRepository products, EntityLookup lookup) { this.products = products; this.lookup = lookup; }

    @Transactional(readOnly = true)
    public Page<ProductDto> list(String search, boolean includeInactive, Pageable pageable) {
        var result = products.search(search == null ? null : search.trim(), includeInactive, pageable);
        return result.map(ProductDto::from);
    }
    @Transactional(readOnly = true)
    public ProductDto get(Long id, boolean includeInactive) {
        Product product = lookup.product(id);
        if (!includeInactive && !product.isActive()) throw new ResourceNotFoundException("Product not found: " + id);
        return ProductDto.from(product);
    }
    @Transactional
    public ProductDto create(ProductRequest request) {
        if (products.existsByCodeIgnoreCase(request.code().trim())) throw new ConflictException("Product code already exists");
        Product p = new Product();
        apply(p, request);
        return ProductDto.from(products.save(p));
    }
    @Transactional
    public ProductDto update(Long id, ProductRequest request) {
        Product p = lookup.product(id);
        if (!p.getCode().equalsIgnoreCase(request.code().trim()) && products.existsByCodeIgnoreCase(request.code().trim()))
            throw new ConflictException("Product code already exists");
        apply(p, request);
        return ProductDto.from(p);
    }
    @Transactional
    public void deactivate(Long id) { lookup.product(id).setActive(false); }

    private void apply(Product p, ProductRequest request) {
        p.setName(request.name().trim());
        p.setCode(request.code().trim().toUpperCase());
        p.setDescription(request.description());
        p.setUnitPrice(request.unitPrice());
        p.setCategory(lookup.category(request.categoryId()));
        p.setMinimumStock(request.minimumStock());
        if (request.active() != null) p.setActive(request.active());
    }
}
