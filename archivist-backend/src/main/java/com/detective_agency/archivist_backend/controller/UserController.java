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
        // @RequestBody говорит Spring: "возьми JSON из тела запроса и преврати его в объект User"

        try {
            User createdUser = userService.registerUser(user);
            // Если регистрация прошла успешно, возвращаем ответ:
            // Статус 201 CREATED и в теле ответа - созданного пользователя (без пароля в идеале, но пока так)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalStateException e) {
            // 3. Если наш сервис "бьёт тревогу" (бросает исключение), мы его ловим
            // и отправляем ответ с ошибкой:
            // Статус 409 CONFLICT и в теле - сообщение из исключения ("Этот логин уже занят!")
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