package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
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
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       UserEventProducer userEventProducer, CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userEventProducer = userEventProducer;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(int id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userService");

        return circuitBreaker.run(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));
            return userMapper.toResponseDto(user);
        }, throwable -> {
            return createFallbackResponse(id, "Сервис временно недоступен. Попробуйте позже.");
        });
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userService");

        return circuitBreaker.run(() -> {
            validateUserData(userRequestDto);

            if (userRepository.existsByEmail(userRequestDto.getEmail())) {
                throw new IllegalArgumentException("Пользователь с email " + userRequestDto.getEmail() + " уже существует");
            }

            LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            User newUser = new User(
                    userRequestDto.getName(),
                    userRequestDto.getEmail(),
                    userRequestDto.getAge(),
                    createdAt
            );

            User savedUser = userRepository.save(newUser);

            circuitBreaker.run(() -> {
                userEventProducer.sendUserCreatedEvent(savedUser.getEmail(), savedUser.getName());
                return null;
            }, kafkaThrowable -> {
                System.err.println("Ошибка отправки Kafka события: " + kafkaThrowable.getMessage());
                return null;
            });

            return userMapper.toResponseDto(savedUser);
        }, throwable -> {
            throw new RuntimeException("Сервис создания пользователей временно недоступен: " + throwable.getMessage());
        });
    }

    private UserResponseDto createFallbackResponse(int id, String message) {
        UserResponseDto fallback = new UserResponseDto();
        fallback.setId(id);
        fallback.setName("Сервис временно недоступен");
        fallback.setEmail("unavailable@example.com");
        fallback.setAge(0);
        fallback.setCreatedAt(LocalDateTime.now());
        return fallback;
    }

    @Transactional
    public UserResponseDto updateUser(int id, UserRequestDto userRequestDto) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userService");

        return circuitBreaker.run(() -> {
            validateUserData(userRequestDto);

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

            user.setName(userRequestDto.getName());
            user.setEmail(userRequestDto.getEmail());
            user.setAge(userRequestDto.getAge());

            User updatedUser = userRepository.save(user);

            return userMapper.toResponseDto(updatedUser);
        }, throwable -> {
            throw new RuntimeException("User update service unavailable: " + throwable.getMessage());
        });
    }

    @Transactional
    public void deleteUser(int id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userService");

        circuitBreaker.run(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));

            String userEmail = user.getEmail();
            String userName = user.getName();

            userRepository.deleteById(id);
            userEventProducer.sendUserDeletedEvent(userEmail, userName);

            return null;
        }, throwable -> {
            throw new RuntimeException("User deletion service unavailable: " + throwable.getMessage());
        });
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