package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserEventProducer userEventProducer;

    @Test
    void findUserById_ExistingUser_ReturnsUser() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        user.setId(1);
        UserResponseDto responseDto = new UserResponseDto(1, "John", "john@test.com", 25, LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());
        assertEquals(25, result.getAge());

        verify(userRepository).findById(1);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void findUserById_NonExistingUser_ThrowsException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(999));

        verify(userRepository).findById(999);
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void createUser_ValidData_CreatesUser() {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 25);
        User savedUser = new User("John", "john@test.com", 25, LocalDateTime.now());
        savedUser.setId(1);
        UserResponseDto responseDto = new UserResponseDto(1, "John", "john@test.com", 25, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());

        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponseDto(savedUser);
    }

    @Test
    void createUser_InvalidName_ThrowsException() {
        UserRequestDto requestDto = new UserRequestDto("", "john@test.com", 25);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void userResponseDto_DefaultConstructor_CreatesEmptyObject() {
        UserResponseDto dto = new UserResponseDto();

        assertNotNull(dto, "Объект должен быть создан");
    }

    @Test
    void createUser_InvalidEmail_ThrowsException() {
        UserRequestDto requestDto = new UserRequestDto("John", "", 25);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidAge_ThrowsException() {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 0);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ExistingUser_UpdatesUser() {
        User existingUser = new User("Old Name", "old@test.com", 20, LocalDateTime.now());
        existingUser.setId(1);
        UserRequestDto requestDto = new UserRequestDto("New Name", "new@test.com", 30);
        User updatedUser = new User("New Name", "new@test.com", 30, existingUser.getCreatedAt());
        updatedUser.setId(1);
        UserResponseDto responseDto = new UserResponseDto(1, "New Name", "new@test.com", 30, LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(1, requestDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userRepository).findById(1);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsException() {
        // Arrange
        UserRequestDto requestDto = new UserRequestDto("New Name", "new@test.com", 30);
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(999, requestDto));

        verify(userRepository).findById(999);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void updateUser_InvalidData_ThrowsException() {
        UserRequestDto requestDto = new UserRequestDto("", "new@test.com", 30);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1, requestDto));

        verify(userRepository, never()).findById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ValidData_SendsKafkaEvent() {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 25);
        User savedUser = new User("John", "john@test.com", 25, LocalDateTime.now());
        savedUser.setId(1);
        UserResponseDto responseDto = new UserResponseDto(1, "John", "john@test.com", 25, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(requestDto);

        verify(userEventProducer).sendUserCreatedEvent("john@test.com", "John");
    }

    @Test
    void deleteUser_ExistingUser_SendsKafkaEvent() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userEventProducer).sendUserDeletedEvent("john@test.com", "John");
        verify(userRepository).deleteById(1);
    }
}