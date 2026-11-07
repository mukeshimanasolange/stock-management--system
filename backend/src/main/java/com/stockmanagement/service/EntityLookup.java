package com.stockmanagement.service;

import com.stockmanagement.entity.*;
import com.stockmanagement.exception.ResourceNotFoundException;
import com.stockmanagement.repository.*;
import org.springframework.stereotype.Component;

@Component
public class EntityLookup {
    private final ProductRepository products;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final SupplierRepository suppliers;
    private final RoleRepository roles;

    public EntityLookup(ProductRepository products, UserRepository users, CategoryRepository categories,
                        SupplierRepository suppliers, RoleRepository roles) {
        this.products = products; this.users = users; this.categories = categories; this.suppliers = suppliers; this.roles = roles;
    }
    public Product product(Long id) {
        return products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
    public Product lockedProduct(Long id) {
        return products.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
    public AppUser user(Long id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
    public AppUser userByEmail(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    public Category category(Long id) {
        return categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }
    public Supplier supplier(Long id) {
        return suppliers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }
    public Role role(String name) {
        return roles.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
    }
}
