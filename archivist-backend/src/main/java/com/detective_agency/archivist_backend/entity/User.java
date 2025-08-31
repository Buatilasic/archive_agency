package com.detective_agency.archivist_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // <-- Импортируем

import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails { // <-- Реализуем интерфейс

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name="Username", length=25, nullable=false, unique=true)
    private String username;

    @Column(name="Email", nullable=false, unique=true)
    private String email;

    @Column(name="Password", length = 64, nullable=false)
    private String passwordHash;

    // --- Добавляем методы, которые требует UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Пока мы не используем роли, возвращаем пустой список
        return null;
    }

    @Override
    public String getPassword() {
        // Возвращаем наш хеш пароля
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    // Далее методы для управления состоянием аккаунта.
    // Пока оставляем их "заглушками", возвращающими true.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}