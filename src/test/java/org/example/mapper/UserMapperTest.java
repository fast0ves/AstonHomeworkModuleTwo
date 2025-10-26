package org.example.mapper;

import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toResponseDto_ValidUser_ReturnsCorrectDto() {
        User user = new User("John", "john@test.com", 25, LocalDateTime.now());
        user.setId(1);

        UserResponseDto result = userMapper.toResponseDto(user);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());
        assertEquals(25, result.getAge());
        assertEquals(user.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void toResponseDto_UserWithNullFields_ReturnsDtoWithNullFields() {
        User user = new User(null, null, 0, null);
        user.setId(0);

        UserResponseDto result = userMapper.toResponseDto(user);

        assertNotNull(result);
        assertEquals(0, result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertEquals(0, result.getAge());
        assertNull(result.getCreatedAt());
    }
}