package com.stockmanagement.config;

import com.stockmanagement.entity.AppUser;
import com.stockmanagement.repository.RoleRepository;
import com.stockmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;
    private final String name;

    public AdminBootstrap(UserRepository users, RoleRepository roles, PasswordEncoder encoder,
            @Value("${app.bootstrap.admin-email:}") String email,
            @Value("${app.bootstrap.admin-password:}") String password,
            @Value("${app.bootstrap.admin-name:Administrator}") String name) {
        this.users = users; this.roles = roles; this.encoder = encoder;
        this.email = email; this.password = password; this.name = name;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) return;
        if (email.isBlank() || password.length() < 12)
            throw new IllegalStateException("Configure both ADMIN_EMAIL and an ADMIN_PASSWORD of at least 12 characters");
        if (users.count() > 0) return;
        AppUser admin = new AppUser();
        admin.setFullName(name); admin.setEmail(email.trim().toLowerCase()); admin.setPassword(encoder.encode(password));
        admin.getRoles().add(roles.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing; import db/schema.sql first")));
        users.save(admin);
    }
}
