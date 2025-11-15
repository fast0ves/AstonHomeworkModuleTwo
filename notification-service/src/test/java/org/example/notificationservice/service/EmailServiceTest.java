package org.example.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_ValidParameters_CallsMailSender() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_NullParameters_CallsMailSender() {
        emailService.sendEmail(null, null, null);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_ValidParameters_CreatesCorrectMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(argThat((SimpleMailMessage message) ->
                to.equals(message.getTo()[0]) &&
                subject.equals(message.getSubject()) &&
                body.equals(message.getText()) &&
                "sorohov344@gmail.com".equals(message.getFrom())
        ));
    }

    @Test
    void sendEmail_WhenMailSenderThrowsException_ThrowsRuntimeException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.sendEmail(to, subject, body));

        assertEquals("Ошибка отправки email", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("SMTP error", exception.getCause().getMessage());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithMultipleRecipients_WorksCorrectly() {
        String to = "test1@example.com,test2@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test

    void sendEmail_EmptyParameters_CallsMailSender() {
        emailService.sendEmail("", "", "");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}