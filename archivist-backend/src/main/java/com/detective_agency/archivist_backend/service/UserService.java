package com.detective_agency.archivist_backend.service;

import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.entity.UserDto;
import com.detective_agency.archivist_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// этот сервис занимается нашими агентами: регистрация, учёт и всё такое.
@Service("us")
public class UserService {

    // наша картотека всех агентов.
    @Autowired
    private UserRepository userRepository;

    // а это наш шифратор для паролей, чтобы всё было секьюрно.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // регистрируем нового агента в системе.
    public User registerUser(User user) {
        // проверяем по картотеке, не занят ли его позывной (логин).
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("этот логин уже занят!");
        }

        // и заодно сверяем, не светился ли уже его e-mail.
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("этот адрес электронной почты уже используется!");
        }

        // самый важный шаг: шифруем пароль. безопасность превыше всего.
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        // заносим нового агента в нашу базу.
        return userRepository.save(user);
    }

    // получаем список всех наших агентов.
    public List<UserDto> getAllUsers() {
        // берём все личные дела из картотеки...
        return userRepository.findAll()
                .stream()
                // ...и для каждого создаём краткую сводку (dto), без секретных данных.
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                // собираем всё обратно в список.
                .collect(Collectors.toList());
    }
}