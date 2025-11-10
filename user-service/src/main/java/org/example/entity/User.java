package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@Schema(description = "Основная сущность пользователя, хранимая в БД")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор")
    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    @Column(nullable = false)
    @Schema(description = "Имя пользователя")
    private String name;
    @Email
    @NotBlank(message = "Email не может быть пустым")
    @Column(nullable = false)
    @Schema(description = "Почта пользователя")
    private String email;

    @Min(value = 1, message = "Возраст не может быть меньше 1")
    @Column(nullable = false)
    @Schema(description = "Возраст пользователя")
    private int age;
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Дата создания пользователя в БД")
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String name, String email, int age, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Пользователь: " + '\n' +
               "id: " + id + '\n' +
               "Имя: " + name + '\n' +
               "email: " + email + '\n' +
               "Возраст: " + age + '\n' +
               "Создан: " + createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && age == user.age && Objects.equals(name, user.name) &&
               Objects.equals(email, user.email) && Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, createdAt);
    }
}