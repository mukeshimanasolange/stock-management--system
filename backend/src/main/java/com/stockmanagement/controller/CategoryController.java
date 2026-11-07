package com.stockmanagement.controller;

import com.stockmanagement.dto.CategoryRequest;
import com.stockmanagement.entity.Category;
import com.stockmanagement.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categories;
    public CategoryController(CategoryRepository categories) { this.categories = categories; }
    @GetMapping public List<Map<String,Object>> list() {
        return categories.findAll().stream().map(c -> Map.<String,Object>of("id", c.getId(), "name", c.getName(),
                "description", c.getDescription() == null ? "" : c.getDescription())).toList();
    }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','STOREKEEPER')")
    public Category create(@Valid @RequestBody CategoryRequest request) {
        Category c = new Category(); c.setName(request.name().trim()); c.setDescription(request.description());
        return categories.save(c);
    }
}
