package com.neurosession.neurosession.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;                   // Either "RESEARCHER" or "ADMIN" for now

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "researcher", cascade = CascadeType.ALL)
    private List<Participant> participants;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}