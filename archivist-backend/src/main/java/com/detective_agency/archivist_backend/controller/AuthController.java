package com.detective_agency.archivist_backend.controller;

// импортируем всё необходимое для работы с аутентификацией, dto и http-ответами.
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

// этот контроллер — наш пропускной пункт, он отвечает за вход и регистрацию.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // подключаем наш сервис по работе с агентами, чтобы регистрировать новичков.
    @Autowired
    private UserService userService;

    // это главный по проверке документов (логина и пароля).
    @Autowired
    private AuthenticationManager authenticationManager;

    // здесь мы принимаем новобранцев.
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // передаём дело новичка в отдел кадров (сервис).
            User createdUser = userService.registerUser(user);
            // готовим краткую выписку по новому агенту для ответа.
            UserDto responseDto = new UserDto(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail()
            );
            // отвечаем, что всё прошло успешно и агент зачислен в штат.
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalStateException e) {
            // если что-то пошло не так (например, позывной занят), формируем отчёт об ошибке.
            ErrorResponseDto errorResponse = new ErrorResponseDto(e.getMessage());
            // и сообщаем, что возник конфликт.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    // а здесь агенты проходят проверку на входе.
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            // предъявляем "документы" (логин и пароль) нашему главному по проверке.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // если проверка прошла, "кладём пропуск" (данные аутентификации) в специальный карман.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // явная команда сохранить "пропуск" в сессии, чтобы агент не представлялся каждый раз.
            SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
            contextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            // ----------------------------------------------------------------------

            // получаем личное дело агента из его "пропуска".
            User user = (User) authentication.getPrincipal();
            // готовим краткую сводку по агенту для ответа.
            UserDto responseDto = new UserDto(user.getId(), user.getUsername(), user.getEmail());

            // сообщаем, что агент успешно вошёл в систему.
            return ResponseEntity.ok(responseDto);
        } catch (BadCredentialsException e) {
            // если документы поддельные (неверный логин или пароль), составляем рапорт "доступ запрещён".
            ErrorResponseDto errorResponse = new ErrorResponseDto("Неверный логин или пароль");
            // и выставляем охрану.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}