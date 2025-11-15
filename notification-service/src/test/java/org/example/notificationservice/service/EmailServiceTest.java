package org.example.notificationservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, circuitBreakerFactory);
    }

    @Test
    void sendEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        when(circuitBreakerFactory.create("emailService")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            java.util.function.Supplier<Void> supplier = invocation.getArgument(0);

            return supplier.get();
        }).when(circuitBreaker).run(any(), any());

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        emailService.sendEmail(to, subject, body);

        verify(circuitBreakerFactory).create("emailService");
        verify(circuitBreaker).run(any(), any());
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertTrue(outContent.toString().contains("Email отправлен на: " + to));

        System.setOut(System.out);
    }

    @Test
    void sendEmail_WithCorrectMessageParameters() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        when(circuitBreakerFactory.create("emailService")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            java.util.function.Supplier<Void> supplier = invocation.getArgument(0);

            return supplier.get();
        }).when(circuitBreaker).run(any(), any());

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(argThat((SimpleMailMessage message) ->
                to.equals(message.getTo()[0]) &&
                subject.equals(message.getSubject()) &&
                body.equals(message.getText()) &&
                "sorohov344@gmail.com".equals(message.getFrom())
        ));
    }

    @Test
    void sendEmail_WhenMailSenderThrowsException_ShouldThrowRuntimeException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        when(circuitBreakerFactory.create("emailService")).thenReturn(circuitBreaker);

        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        doAnswer(invocation -> {
            java.util.function.Supplier<Void> supplier = invocation.getArgument(0);

            return supplier.get();
        }).when(circuitBreaker).run(any(), any());

        java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(errContent));

        assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(to, subject, body)
        );

        assertTrue(errContent.toString().contains("Ошибка отправки email"));

        System.setErr(System.err);
    }

    @Test
    void sendEmail_WhenCircuitBreakerFallback_ShouldHandleGracefully() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        when(circuitBreakerFactory.create("emailService")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            java.util.function.Function<Throwable, Void> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Service unavailable"));
        }).when(circuitBreaker).run(any(), any());

        java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(errContent));

        emailService.sendEmail(to, subject, body);

        verify(circuitBreakerFactory).create("emailService");
        verify(circuitBreaker).run(any(), any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Email service unavailable"));
        assertTrue(errorOutput.contains(to));

        System.setErr(System.err);
    }

    @Test
    void sendEmail_WithMultipleRecipients() {
        String to = "test1@example.com,test2@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        when(circuitBreakerFactory.create("emailService")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            java.util.function.Supplier<Void> supplier = invocation.getArgument(0);
            return supplier.get();
        }).when(circuitBreaker).run(any(), any());

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(argThat((SimpleMailMessage message) ->
                to.equals(message.getTo()[0])
        ));
    }

    @Test
    void constructor_WithValidDependencies_ShouldCreateInstance() {
        assertNotNull(emailService);
        assertDoesNotThrow(() -> new EmailService(mailSender, circuitBreakerFactory));
    }
}