package com.stockmanagement.service;

import com.stockmanagement.dto.*;
import com.stockmanagement.entity.AppUser;
import com.stockmanagement.exception.*;
import com.stockmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository users;
    private final EntityLookup lookup;
    private final PasswordEncoder encoder;
    public UserService(UserRepository users, EntityLookup lookup, PasswordEncoder encoder) {
        this.users = users; this.lookup = lookup; this.encoder = encoder;
    }
    public List<UserDto> all() { return users.findAll().stream().map(UserDto::from).toList(); }
    public UserDto get(Long id) { return UserDto.from(lookup.user(id)); }
    @Transactional
    public UserDto create(UserUpsertRequest request) {
        if (request.password() == null || request.password().isBlank()) throw new BadRequestException("Password is required");
        if (users.existsByEmailIgnoreCase(request.email())) throw new ConflictException("Email is already registered");
        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim()); user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(encoder.encode(request.password()));
        user.setActive(request.active() == null || request.active());
        setRoles(user, request.roles());
        return UserDto.from(users.save(user));
    }
    @Transactional
    public UserDto update(Long id, UserUpsertRequest request) {
        AppUser user = lookup.user(id);
        if (!user.getEmail().equalsIgnoreCase(request.email()) && users.existsByEmailIgnoreCase(request.email()))
            throw new ConflictException("Email is already registered");
        user.setFullName(request.fullName().trim()); user.setEmail(request.email().trim().toLowerCase());
        if (request.password() != null && !request.password().isBlank()) user.setPassword(encoder.encode(request.password()));
        if (request.active() != null) user.setActive(request.active());
        if (request.roles() != null) setRoles(user, request.roles());
        return UserDto.from(user);
    }
    @Transactional
    public void deactivate(Long id) { lookup.user(id).setActive(false); }
    private void setRoles(AppUser user, java.util.Set<String> roleNames) {
        var names = roleNames == null || roleNames.isEmpty() ? java.util.Set.of("STAFF") : roleNames;
        user.setRoles(names.stream().map(name -> lookup.role(name.trim().toUpperCase())).collect(java.util.stream.Collectors.toSet()));
    }
}
