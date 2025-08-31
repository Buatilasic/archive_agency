package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.entity.UserDto;
import com.detective_agency.archivist_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Общий "адрес" для всех методов, связанных с пользователями
public class UserController {

    // 1. Вызываем нашего оперативника - UserService
    @Autowired
    private UserService userService;

    // 2. Указываем, что это POST-запрос по адресу /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);

            // --- Вот исправление ---
            // 1. Создаём DTO из полученной сущности
            UserDto responseDto = new UserDto(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail()
            );

            // 2. Отправляем в ответе безопасный DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    // В классе UserController

    @GetMapping // Это GET-запрос по адресу /api/users
    public List<UserDto> getAllUsers() {
        // Вызываем метод сервиса, который мы только что создали
        return userService.getAllUsers();
    }
}