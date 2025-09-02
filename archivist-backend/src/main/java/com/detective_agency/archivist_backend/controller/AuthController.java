package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.dto.ErrorResponseDto;
import com.detective_agency.archivist_backend.dto.LoginRequestDto;
import com.detective_agency.archivist_backend.entity.UserDto;
import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);
            UserDto responseDto = new UserDto(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalStateException e) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Помещаем аутентификацию в SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // --- ЯВНЫЙ ПРИКАЗ СОХРАНИТЬ КОНТЕКСТ В СЕССИЮ (Решение со Stack Overflow) ---
            SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
            contextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            // ----------------------------------------------------------------------

            User user = (User) authentication.getPrincipal();
            UserDto responseDto = new UserDto(user.getId(), user.getUsername(), user.getEmail());

            return ResponseEntity.ok(responseDto);
        } catch (BadCredentialsException e) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("Неверный логин или пароль");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}