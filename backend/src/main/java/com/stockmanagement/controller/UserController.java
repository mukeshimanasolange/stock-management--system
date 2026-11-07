package com.stockmanagement.controller;

import com.stockmanagement.dto.*;
import com.stockmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @GetMapping public List<UserDto> all() { return service.all(); }
    @GetMapping("/{id}") public UserDto get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public UserDto create(@Valid @RequestBody UserUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserDto update(@PathVariable Long id, @Valid @RequestBody UserUpsertRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deactivate(id); }
}
