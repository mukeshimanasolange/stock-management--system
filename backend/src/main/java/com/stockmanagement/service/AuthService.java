package com.stockmanagement.service;

import com.stockmanagement.dto.AuthDtos.*;
import com.stockmanagement.dto.UserDto;
import com.stockmanagement.entity.AppUser;
import com.stockmanagement.exception.ConflictException;
import com.stockmanagement.repository.UserRepository;
import com.stockmanagement.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final EntityLookup lookup;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository users, EntityLookup lookup, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users; this.lookup = lookup; this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager; this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ConflictException("Email is already registered");
        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getRoles().add(lookup.role("STAFF"));
        users.save(user);
        return new AuthResponse(jwtService.generateToken(user), UserDto.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUser user = lookup.userByEmail(request.email());
        return new AuthResponse(jwtService.generateToken(user), UserDto.from(user));
    }
}
