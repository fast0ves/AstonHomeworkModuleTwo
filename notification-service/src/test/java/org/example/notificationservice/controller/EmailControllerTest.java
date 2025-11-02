package org.example.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notificationservice.service.EmailService;
import org.example.notificationservice.dto.EmailRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@ContextConfiguration(classes = {EmailController.class})
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_ValidRequest_ReturnsSuccess() throws Exception {
        EmailRequestDto requestDto = new EmailRequestDto("test@example.com", "Test Subject", "Test Body");
        doNothing().when(emailService).sendEmail("test@example.com", "Test Subject", "Test Body");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully to: test@example.com"));
    }

    @Test
    void sendEmail_ServiceThrowsException_ReturnsBadRequest() throws Exception {
        EmailRequestDto requestDto = new EmailRequestDto("test@example.com", "Test Subject", "Test Body");
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailService).sendEmail("test@example.com", "Test Subject", "Test Body");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to send email: Email service unavailable"));
    }
}