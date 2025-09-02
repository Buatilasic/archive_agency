package com.detective_agency.archivist_backend.utils;

import com.detective_agency.archivist_backend.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils { // final, так как от него не нужно наследоваться

    private SecurityUtils() { // Приватный конструктор, чтобы нельзя было создать объект
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null; // Или бросать исключение, если анонимный доступ запрещён
        }
        return (User) authentication.getPrincipal();
    }
}