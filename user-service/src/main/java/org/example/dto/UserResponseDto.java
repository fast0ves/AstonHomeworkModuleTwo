package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class UserResponseDto extends RepresentationModel<UserResponseDto> {
    @Schema(description = "Уникальный идентификатор пользователя")
    private int id;

    @Schema(description = "Имя пользователя")
    private String name;

    @Email
    @Schema(description = "Email пользователя")
    private String email;

    @Schema(description = "Возраст пользователя")
    private int age;

    @Schema(description = "Время создания пользователя")
    private LocalDateTime createdAt;

    public UserResponseDto() {}

    public UserResponseDto(int id, String name, String email, int age, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
