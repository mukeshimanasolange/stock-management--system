package com.stockmanagement.controller;

import com.stockmanagement.dto.AuthDtos.*;
import com.stockmanagement.dto.UserDto;
import com.stockmanagement.service.AuthService;
import com.stockmanagement.service.EntityLookup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final EntityLookup lookup;
    public AuthController(AuthService auth, EntityLookup lookup) { this.auth = auth; this.lookup = lookup; }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterBody body) {
        return auth.register(new RegisterRequest(body.fullName(), body.email(), body.password()));
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginBody body) {
        return auth.login(new LoginRequest(body.email(), body.password()));
    }
    @GetMapping("/me")
    public UserDto me(Authentication authentication) {
        return UserDto.from(lookup.userByEmail(authentication.getName()));
    }
    public record RegisterBody(@NotBlank @Size(max=120) String fullName, @NotBlank @Email @Size(max=190) String email,
                               @NotBlank @Size(min=8,max=72) String password) {}
    public record LoginBody(@NotBlank @Email String email, @NotBlank String password) {}
}
