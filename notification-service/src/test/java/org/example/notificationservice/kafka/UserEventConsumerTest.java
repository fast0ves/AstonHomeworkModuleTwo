package org.example.notificationservice.kafka;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    private UserEventConsumer userEventConsumer;

    @BeforeEach
    void setUp() {
        userEventConsumer = new UserEventConsumer(emailService, circuitBreakerFactory);
    }

    @Test
    void consumeUserEvent_WhenEmailServiceThrowsException_ShouldLogAndRethrow() {
        // Arrange
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("CREATE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doThrow(new RuntimeException("Email service error")).when(emailService)
                .sendEmail(eq("test@example.com"), anyString(), anyString());

        doAnswer(invocation -> {
            try {
                return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
            } catch (Exception e) {
                throw e;
            }
        }).when(circuitBreaker).run(any(), any());

        UserEventConsumer spyConsumer = spy(userEventConsumer);
        Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

        try {
            spyConsumer.consumeUserEvent(userEvent);
        } catch (RuntimeException e) {
            verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
        }
    }

    @Test
    void consumeUserEvent_WhenCreateOperationAndEmailFails_ShouldCatchAndRethrowException() {
        // Arrange
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("CREATE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doThrow(new RuntimeException("SMTP connection failed"))
                .when(emailService)
                .sendEmail(eq("test@example.com"), anyString(), anyString());

        doAnswer(invocation -> {
            try {

                return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
            } catch (Exception e) {
                throw e;
            }
        }).when(circuitBreaker).run(any(), any());

        assertThrows(RuntimeException.class, () ->
                userEventConsumer.consumeUserEvent(userEvent)
        );

        verify(circuitBreakerFactory).create("kafkaConsumer");
        verify(circuitBreaker).run(any(), any());
    }

    @Test
    void consumeUserEvent_WhenDeleteOperationAndEmailFails_ShouldCatchAndRethrowException() {
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("DELETE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doThrow(new RuntimeException("Mail server unavailable"))
                .when(emailService)
                .sendEmail(eq("test@example.com"), anyString(), anyString());

        doAnswer(invocation -> {
            try {

                return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
            } catch (Exception e) {
                throw e;
            }
        }).when(circuitBreaker).run(any(), any());

        assertThrows(RuntimeException.class, () ->
                userEventConsumer.consumeUserEvent(userEvent)
        );

        verify(circuitBreakerFactory).create("kafkaConsumer");
        verify(circuitBreaker).run(any(), any());
    }

    @Test
    void consumeUserEvent_WhenUnexpectedExceptionInProcessing_ShouldLogAndRethrow() {
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("CREATE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doThrow(new NullPointerException("Unexpected NPE"))
                .when(emailService)
                .sendEmail(eq("test@example.com"), anyString(), anyString());

        doAnswer(invocation -> {
            try {

                return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
            } catch (Exception e) {
                throw e;
            }
        }).when(circuitBreaker).run(any(), any());

        assertThrows(NullPointerException.class, () ->
                userEventConsumer.consumeUserEvent(userEvent)
        );

        verify(circuitBreakerFactory).create("kafkaConsumer");
        verify(circuitBreaker).run(any(), any());
    }

    @Test
    void consumeUserEvent_WhenCircuitBreakerFallbackDueToException_ShouldCallFallback() {
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("CREATE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {
            java.util.function.Function<Throwable, Void> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Circuit breaker opened"));
        }).when(circuitBreaker).run(any(), any());

        userEventConsumer.consumeUserEvent(userEvent);

        verify(circuitBreakerFactory).create("kafkaConsumer");
        verify(circuitBreaker).run(any(), any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void consumeUserEvent_WhenCreateOperationSuccess_ShouldSendWelcomeEmail() {
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("CREATE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {

            return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
        }).when(circuitBreaker).run(any(), any());

        userEventConsumer.consumeUserEvent(userEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                eq("Здравствуйте, Test User! Ваш аккаунт был успешно создан.")
        );
    }

    @Test
    void consumeUserEvent_WhenDeleteOperationSuccess_ShouldSendGoodbyeEmail() {
        UserEventDto userEvent = new UserEventDto();
        userEvent.setOperation("DELETE");
        userEvent.setEmail("test@example.com");
        userEvent.setUserName("Test User");

        when(circuitBreakerFactory.create("kafkaConsumer")).thenReturn(circuitBreaker);

        doAnswer(invocation -> {

            return invocation.<java.util.function.Supplier<Void>>getArgument(0).get();
        }).when(circuitBreaker).run(any(), any());

        userEventConsumer.consumeUserEvent(userEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт удален"),
                eq("Здравствуйте, Test User! Ваш аккаунт был удалён.")
        );
    }
}