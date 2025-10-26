package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

        return userMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validateUserData(userRequestDto);

        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        User newUser = new User(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                userRequestDto.getAge(),
                createdAt
        );

        User savedUser = userRepository.save(newUser);

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto updateUser(int id, UserRequestDto userRequestDto) {
        validateUserData(userRequestDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setAge(userRequestDto.getAge());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    private void validateUserData(UserRequestDto userRequestDto) {
        if (userRequestDto.getName() == null || userRequestDto.getName().isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (userRequestDto.getAge() <= 0) {
            throw new IllegalArgumentException("Возраст должен быть положительным числом");
        }
    }
}