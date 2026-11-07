package com.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "suppliers")
public class Supplier extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(length = 40)
    private String phone;
    @Column(length = 190)
    private String email;
    @Column(length = 500)
    private String address;
    @Column(name = "company_name", length = 150)
    private String companyName;
    @Column(nullable = false)
    private boolean active = true;
}
