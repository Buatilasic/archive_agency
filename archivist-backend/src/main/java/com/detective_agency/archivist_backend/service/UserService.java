package com.detective_agency.archivist_backend.service;

import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.entity.UserDto;
import com.detective_agency.archivist_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Импортируем наш инструмент
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("us")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalStateException("Этот логин уже занят!");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("Этот адрес электронной почты уже используется!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll() // Получаем список всех объектов User
                .stream() // Превращаем в поток для удобной обработки
                // Для каждого объекта User создаём новый объект UserDto
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                // Собираем всё обратно в список
                .collect(Collectors.toList());
    }
}