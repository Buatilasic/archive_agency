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
// диспетчер, отвечающий за работу с личными делами агентов (пользователями).
@RequestMapping("/api/users")
public class UserController {

    // подключаем сервис, который непосредственно работает с базой агентов.
    @Autowired
    private UserService userService;

    // обрабатывает post-запрос на регистрацию нового агента.
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);

            // важный момент: в ответ отправляем не всю сущность user, а только безопасный dto.
            // это чтобы случайно не "засветить" хэш пароля.
            UserDto responseDto = new UserDto(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail()
            );

            // возвращаем статус 'создано' и данные нового агента.
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalStateException e) {
            // если логин или почта заняты, сервис бросит ошибку, а мы сообщим о конфликте.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // get-запрос для получения списка всех зарегистрированных агентов.
    @GetMapping
    public List<UserDto> getAllUsers() {
        // сервис сразу возвращает список в виде dto, так что просто передаём его дальше.
        return userService.getAllUsers();
    }
}