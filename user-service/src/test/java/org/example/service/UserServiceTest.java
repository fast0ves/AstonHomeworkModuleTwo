package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, userEventProducer, circuitBreakerFactory);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
    }
    @Test
    void validateUserData_ThroughCreateUser_ShouldThrowExceptions() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            try {
                return invocation.getArgument(0, java.util.function.Supplier.class).get();
            } catch (IllegalArgumentException e) {
                throw e;
            }
        });

        UserRequestDto invalidName = new UserRequestDto();
        invalidName.setName("");
        invalidName.setEmail("test@example.com");
        invalidName.setAge(25);

        assertThatThrownBy(() -> userService.createUser(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Имя не может быть пустым");

        UserRequestDto invalidEmail = new UserRequestDto();
        invalidEmail.setName("Test Name");
        invalidEmail.setEmail("");
        invalidEmail.setAge(25);

        assertThatThrownBy(() -> userService.createUser(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email не может быть пустым");

        UserRequestDto invalidAge = new UserRequestDto();
        invalidAge.setName("Test Name");
        invalidAge.setEmail("test@example.com");
        invalidAge.setAge(0);

        assertThatThrownBy(() -> userService.createUser(invalidAge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Возраст должен быть положительным числом");
    }


    // Остальные тесты остаются без изменений
    @Test
    void findUserById_Success() {
        int userId = 1;
        User user = new User("John Doe", "john@example.com", 30, LocalDateTime.now());
        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Doe");
        expectedResponse.setEmail("john@example.com");
        expectedResponse.setAge(30);

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);

        UserResponseDto result = userService.findUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDto(user);
        verify(circuitBreakerFactory).create("userService");
    }

    @Test
    void findUserById_WhenUserNotFound_ShouldThrowException() {
        int userId = 999;

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с id " + userId + " не найден");

        verify(userRepository).findById(userId);
    }

    @Test
    void findUserById_WhenCircuitBreakerFallback_ShouldReturnFallbackResponse() {
        int userId = 1;

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Function.class)
                    .apply(new RuntimeException("Service unavailable"));
        });

        UserResponseDto result = userService.findUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("Сервис временно недоступен");
        assertThat(result.getEmail()).isEqualTo("unavailable@example.com");
        assertThat(result.getAge()).isEqualTo(0);
        assertThat(result.getCreatedAt()).isNotNull();

        verify(circuitBreakerFactory).create("userService");
        verify(userRepository, never()).findById(anyInt());
    }

    @Test
    void createUser_Success() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("jane@example.com");
        requestDto.setAge(25);

        User savedUser = new User("Jane Doe", "jane@example.com", 25, LocalDateTime.now());
        savedUser.setId(1);
        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1);
        expectedResponse.setName("Jane Doe");
        expectedResponse.setEmail("jane@example.com");
        expectedResponse.setAge(25);

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.createUser(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");

        verify(userRepository).existsByEmail("jane@example.com");
        verify(userRepository).save(any(User.class));
        verify(userEventProducer).sendUserCreatedEvent("jane@example.com", "Jane Doe");
        verify(userMapper).toResponseDto(savedUser);
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("jane@example.com");
        requestDto.setAge(25);

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с email jane@example.com уже существует");

        verify(userRepository).existsByEmail("jane@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(userEventProducer, never()).sendUserCreatedEvent(anyString(), anyString());
    }

    @Test
    void createUser_WhenValidationFails_ShouldThrowException() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName(""); // Invalid name
        requestDto.setEmail("jane@example.com");
        requestDto.setAge(25);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Имя не может быть пустым");
    }

    @Test
    void createUser_WhenKafkaFails_ShouldContinueNormally() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("jane@example.com");
        requestDto.setAge(25);

        User savedUser = new User("Jane Doe", "jane@example.com", 25, LocalDateTime.now());
        savedUser.setId(1);
        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1);
        expectedResponse.setName("Jane Doe");
        expectedResponse.setEmail("jane@example.com");
        expectedResponse.setAge(25);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        }).doAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Function.class)
                    .apply(new RuntimeException("Kafka unavailable"));
        }).when(circuitBreaker).run(any(), any());

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(errContent));

        UserResponseDto result = userService.createUser(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");

        String errorOutput = errContent.toString();
        assertThat(errorOutput).contains("Ошибка отправки Kafka события");
        assertThat(errorOutput).contains("Kafka unavailable");

        System.setErr(System.err);

        verify(userRepository).existsByEmail("jane@example.com");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponseDto(savedUser);
        verify(userEventProducer, never()).sendUserCreatedEvent(anyString(), anyString());
    }

    @Test
    void createUser_WhenCircuitBreakerFallback_ShouldThrowRuntimeException() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("jane@example.com");
        requestDto.setAge(25);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Function.class)
                    .apply(new RuntimeException("Database unavailable"));
        });

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Сервис создания пользователей временно недоступен");

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        int userId = 1;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setEmail("updated@example.com");
        requestDto.setAge(35);

        User existingUser = new User("Old Name", "old@example.com", 30, LocalDateTime.now());
        existingUser.setId(userId);
        User updatedUser = new User("Updated Name", "updated@example.com", 35, LocalDateTime.now());
        updatedUser.setId(userId);
        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("Updated Name");
        expectedResponse.setEmail("updated@example.com");
        expectedResponse.setAge(35);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.updateUser(userId, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getAge()).isEqualTo(35);

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        int userId = 999;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setEmail("updated@example.com");
        requestDto.setAge(35);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с id " + userId + " не найден");
    }

    @Test
    void deleteUser_Success() {
        int userId = 1;
        User user = new User("John Doe", "john@example.com", 30, LocalDateTime.now());
        user.setId(userId);

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
        verify(userEventProducer).sendUserDeletedEvent("john@example.com", "John Doe");
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        int userId = 999;

        when(circuitBreakerFactory.create("userService")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, java.util.function.Supplier.class).get();
        });
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователь с id " + userId + " не найден");

        verify(userRepository, never()).deleteById(anyInt());
        verify(userEventProducer, never()).sendUserDeletedEvent(anyString(), anyString());
    }

    @Test
    void validateUserData_ValidData_ShouldNotThrowException() {
        // Arrange
        UserRequestDto validRequest = new UserRequestDto();
        validRequest.setName("Valid Name");
        validRequest.setEmail("valid@example.com");
        validRequest.setAge(25);

        userService.createUser(validRequest);
    }

    @Test
    void validateUserData_InvalidData_ShouldThrowExceptions() {
        // Настраиваем CircuitBreaker для проброса исключений валидации
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            try {
                return invocation.getArgument(0, java.util.function.Supplier.class).get();
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                return invocation.getArgument(1, java.util.function.Function.class).apply(e);
            }
        });

        // Test empty name
        UserRequestDto invalidName = new UserRequestDto();
        invalidName.setName("");
        invalidName.setEmail("test@example.com");
        invalidName.setAge(25);

        assertThatThrownBy(() -> userService.createUser(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Имя не может быть пустым");

        UserRequestDto invalidEmail = new UserRequestDto();
        invalidEmail.setName("Test Name");
        invalidEmail.setEmail("");
        invalidEmail.setAge(25);

        assertThatThrownBy(() -> userService.createUser(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email не может быть пустым");

        UserRequestDto invalidAge = new UserRequestDto();
        invalidAge.setName("Test Name");
        invalidAge.setEmail("test@example.com");
        invalidAge.setAge(0);

        assertThatThrownBy(() -> userService.createUser(invalidAge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Возраст должен быть положительным числом");
    }

    @Test
    void updateUser_ShouldCreateCircuitBreakerWithCorrectName() {
        int userId = 1;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setEmail("updated@example.com");
        requestDto.setAge(35);

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Function.class)
                    .apply(new RuntimeException("Test error"));
        });

        assertThatThrownBy(() -> userService.updateUser(userId, requestDto))
                .isInstanceOf(RuntimeException.class);

        verify(circuitBreakerFactory).create("userService");
    }

    @Test
    void deleteUser_ShouldCreateCircuitBreakerWithCorrectName() {
        int userId = 1;

        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Function.class)
                    .apply(new RuntimeException("Test error"));
        });

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class);

        verify(circuitBreakerFactory).create("userService");
    }
}