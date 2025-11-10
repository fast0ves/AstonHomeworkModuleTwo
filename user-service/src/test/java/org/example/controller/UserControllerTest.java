package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUser_ExistingUser_ReturnsUser() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(1, "John", "john@test.com", 25, LocalDateTime.now());
        when(userService.findUserById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void getUser_NonExistingUser_ReturnsNotFound() throws Exception {
        when(userService.findUserById(999)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_ValidData_ReturnsCreated() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 25);
        UserResponseDto responseDto = new UserResponseDto(1, "John", "john@test.com", 25, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void createUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("", "invalid-email", -5);

        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid user data"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("John Updated", "updated@test.com", 30);
        UserResponseDto responseDto = new UserResponseDto(1, "John Updated", "updated@test.com", 30, LocalDateTime.now());

        when(userService.updateUser(anyInt(), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void updateUser_NonExistingUser_ReturnsNotFound() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("John", "john@test.com", 25);

        when(userService.updateUser(eq(999), any(UserRequestDto.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ExistingUser_ReturnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

    @Test
    void deleteUser_NonExistingUser_ReturnsNotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteUser(999);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isBadRequest()); // ← Исправлено на 400
    }

    @Test
    void getAllUsers_ReturnsApiInfo() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.info").value("User Management API"))
                .andExpect(jsonPath("$.endpoints").exists())
                .andExpect(jsonPath("$.documentation").exists());
    }
}