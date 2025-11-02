package org.example.notificationservice.kafka;

import org.example.notificationservice.service.EmailService;
import org.example.notificationservice.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    void consumeUserEvent_CreateOperation_SendsWelcomeEmail() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(createEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                contains("Ваш аккаунт на сайте ваш сайт был успешно создан.")
        );
    }

    @Test
    void consumeUserEvent_DeleteOperation_SendsDeletionEmail() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "test@example.com", "John Doe");
        userEventConsumer.consumeUserEvent(deleteEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт удален"),
                contains("Ваш аккаунт был удалён.")
        );
    }

    @Test
    void consumeUserEvent_UnknownOperation_DoesNotSendEmail() {
        UserEventDto unknownEvent = new UserEventDto("UNKNOWN", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(unknownEvent);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void consumeUserEvent_WhenEmailServiceThrowsException_LogsErrorAndContinues() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");

        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        userEventConsumer.consumeUserEvent(createEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                contains("Ваш аккаунт на сайте ваш сайт был успешно создан.")
        );
    }

    @Test
    void consumeUserEvent_NullUserName_HandlesCorrectly() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", null);

        userEventConsumer.consumeUserEvent(createEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                contains("Здравствуйте, null! Ваш аккаунт на сайте ваш сайт был успешно создан.")
        );
    }

    @Test
    void consumeUserEvent_EmptyUserName_HandlesCorrectly() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "");

        userEventConsumer.consumeUserEvent(createEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                contains("Здравствуйте, ! Ваш аккаунт на сайте ваш сайт был успешно создан.")
        );
    }

    @Test
    void consumeUserEvent_NullOperation_DoesNotSendEmail() {
        UserEventDto nullOperationEvent = new UserEventDto(null, "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(nullOperationEvent);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void consumeUserEvent_DeleteOperationWithNullUserName_HandlesCorrectly() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "test@example.com", null);

        userEventConsumer.consumeUserEvent(deleteEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт удален"),
                contains("Здравствуйте, null! Ваш аккаунт был удалён.")
        );
    }
}