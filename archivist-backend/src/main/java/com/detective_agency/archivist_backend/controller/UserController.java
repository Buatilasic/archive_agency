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


    // get-запрос для получения списка всех зарегистрированных агентов.
    @GetMapping
    public List<UserDto> getAllUsers() {
        // сервис сразу возвращает список в виде dto, так что просто передаём его дальше.
        return userService.getAllUsers();
    }
}