package com.detective_agency.archivist_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users") // Явно указываем имя таблицы для надёжности
public class User implements UserDetails { // <-- ГЛАВНАЯ ПРАВКА

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY - лучший выбор для Postgres
    private long id;

    @Column(name = "username", length = 25, nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 64, nullable = false)
    private String passwordHash;

    // --- МЕТОДЫ, КОТОРЫЕ ТРЕБУЕТ UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Мы пока не используем роли, поэтому возвращаем пустой список.
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    // Эти методы пока просто возвращают true.
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}