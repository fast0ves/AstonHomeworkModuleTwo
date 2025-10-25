package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
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

    @InjectMocks
    private UserService userService;

    @Test
    void findUserById_ExistingUser_ReturnsUser() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());
        assertEquals(25, result.getAge());
    }

    @Test
    void findUserById_NonExistingUser_ReturnsNull() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        UserResponseDto result = userService.findUserById(999);

        assertNull(result);
    }

    @Test
    void createUser_ValidData_CreatesUser() {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 25);
        User savedUser = new User("John", "john@test.com", 25, LocalDateTime.now());
        savedUser.setId(1);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
    }

    @Test
    void createUser_InvalidData_ThrowsException() {
        UserRequestDto requestDto = new UserRequestDto("", "john@test.com", 25);

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

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDto result = userService.updateUser(1, requestDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(30, result.getAge());
    }

    @Test
    void deleteUser_ExistingUser_ReturnsTrue() {
        when(userRepository.existsById(1)).thenReturn(true);

        boolean result = userService.deleteUser(1);

        assertTrue(result);
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_NonExistingUser_ReturnsFalse() {
        when(userRepository.existsById(999)).thenReturn(false);

        boolean result = userService.deleteUser(999);

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyInt());
    }
}