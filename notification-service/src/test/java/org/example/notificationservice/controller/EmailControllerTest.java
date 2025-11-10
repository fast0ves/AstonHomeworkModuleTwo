package org.example.notificationservice.controller;
import org.example.notificationservice.service.EmailService;
import org.example.notificationservice.dto.EmailRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    void sendEmail_ValidRequest_Success() throws Exception {
        EmailRequestDto requestDto = new EmailRequestDto("test@example.com", "Test Subject", "Test Body");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "to": "test@example.com",
                                "subject": "Test Subject",
                                "body": "Test Body"
                            }
                        """))
                .andExpect(status().isOk());

        verify(emailService).sendEmail("test@example.com", "Test Subject", "Test Body");
    }

    @Test
    void sendUserCreatedEmail_ValidParameters_Success() throws Exception {
        mockMvc.perform(post("/api/notifications/user-created")
                        .param("email", "test@example.com")
                        .param("userName", "John Doe"))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                "test@example.com",
                "Добро пожаловать!",
                "Здравствуйте, John Doe! Ваш аккаунт на сайте был успешно создан."
        );
    }

    @Test
    void sendUserDeletedEmail_ValidParameters_Success() throws Exception {
        mockMvc.perform(post("/api/notifications/user-deleted")
                        .param("email", "test@example.com")
                        .param("userName", "John Doe"))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                "test@example.com",
                "Аккаунт удален",
                "Здравствуйте, John Doe! Ваш аккаунт был удалён."
        );
    }

    @Test
    void healthCheck_ReturnsServiceStatus() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification service is running"));
    }

    @Test
    void sendUserCreatedEmail_WithNullUserName_Success() throws Exception {
        mockMvc.perform(post("/api/notifications/user-created")
                        .param("email", "test@example.com")
                        .param("userName", ""))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                "test@example.com",
                "Добро пожаловать!",
                "Здравствуйте, ! Ваш аккаунт на сайте был успешно создан."
        );
    }

    @Test
    void sendUserDeletedEmail_WithSpecialCharacters_Success() throws Exception {
        mockMvc.perform(post("/api/notifications/user-deleted")
                        .param("email", "test@example.com")
                        .param("userName", "Иван Иванов"))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(
                "test@example.com",
                "Аккаунт удален",
                "Здравствуйте, Иван Иванов! Ваш аккаунт был удалён."
        );
    }
}