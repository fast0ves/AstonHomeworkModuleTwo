package org.example.controller;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить пользователя по ID", description = "Позволяет получить пользователя по ID")
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable int id) {
        UserResponseDto user = userService.findUserById(id);

        user.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        user.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("collection"));

        return user;
    }


    @Operation(summary = "Создать нового пользователя", description = "Позволяет создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        createdUser.add(linkTo(methodOn(UserController.class).getUser(createdUser.getId())).withSelfRel());
        createdUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("collection"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @Operation(summary = "Обновить пользователя", description = "Позволяет обновить пользователя")
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable int id, @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);

        updatedUser.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        updatedUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("collection"));

        return updatedUser;
    }

    @Operation(summary = "Удалить пользователя", description = "Позволяет удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить информацию о API", description = "Получение информации об API")
    @GetMapping
    public Map<String, String> getAllUsers() {

        return Map.of(
                "info", "User Management API",
                "endpoints", "GET/POST /api/users, GET/PUT/DELETE /api/users/{id}",
                "documentation", "/swagger-ui/index.html"
        );
    }
}