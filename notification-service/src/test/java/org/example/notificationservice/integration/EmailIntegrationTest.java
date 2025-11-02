package org.example.notificationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notificationservice.controller.EmailController;
import org.example.notificationservice.dto.EmailRequestDto;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@ContextConfiguration(classes = EmailController.class)
class EmailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendEmail_ValidRequest_ReturnsSuccess() throws Exception {
        EmailRequestDto requestDto = new EmailRequestDto(
                "integration-test@example.com",
                "Integration Test",
                "This is an integration test"
        );

        doNothing().when(emailService).sendEmail(
                "integration-test@example.com",
                "Integration Test",
                "This is an integration test"
        );

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully to: integration-test@example.com"));
    }

    @Test
    void sendEmail_InvalidEmail_ReturnsSuccess() throws Exception {
        EmailRequestDto requestDto = new EmailRequestDto(
                "",
                "Test Subject",
                "Test Body"
        );

        doNothing().when(emailService).sendEmail("", "Test Subject", "Test Body");
        
        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}