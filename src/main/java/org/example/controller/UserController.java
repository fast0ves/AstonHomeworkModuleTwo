package org.example.controller;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
        UserResponseDto user = userService.findUserById(id);
        if (user != null) {

            return ResponseEntity.ok(user);
        } else {

            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable int id, @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}