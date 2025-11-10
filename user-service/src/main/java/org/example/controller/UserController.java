package org.example.controller;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить пользователя по ID",
    description = "Позволяет получить пользователя по ID")
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable int id) {
        return userService.findUserById(id);
    }

    @Operation(summary = "Создать нового пользователя",
    description = "Позволяет создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Обновить пользователя",
    description = "Позволяет обновить пользователя")
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable int id, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(id, userRequestDto);
    }

    @Operation(summary = "Удалить пользователя",
    description = "Позволяет удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить информацию о API",
    description = "Получение информации об API")
    @GetMapping
    public Map<String, String> getAllUsers() {
        return Map.of(
                "info", "User Management API",
                "endpoints", "GET/POST /api/users, GET/PUT/DELETE /api/users/{id}",
                "documentation", "/swagger-ui/index.html"
        );
    }
}