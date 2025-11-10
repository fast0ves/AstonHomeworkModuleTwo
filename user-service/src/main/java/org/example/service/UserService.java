package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userEventProducer = userEventProducer;
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

        userEventProducer.sendUserCreatedEvent(savedUser.getEmail(), savedUser.getName());

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto updateUser(int id, UserRequestDto userRequestDto) {
        validateUserData(userRequestDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

        String userEmail = user.getEmail();
        String userName = user.getName();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setAge(userRequestDto.getAge());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

        String userEmail = user.getEmail();
        String userName = user.getName();

        userRepository.deleteById(id);

        userEventProducer.sendUserDeletedEvent(userEmail, userName);
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