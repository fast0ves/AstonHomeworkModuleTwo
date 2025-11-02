package org.example.notificationservice.integration;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.kafka.UserEventConsumer;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaIntegrationTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    void whenCreateUserEventSent_thenEmailServiceCalled() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(createEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Добро пожаловать!"),
                eq("Здравствуйте, John Doe! Ваш аккаунт на сайте ваш сайт был успешно создан.")
        );
    }

    @Test
    void whenDeleteUserEventSent_thenEmailServiceCalled() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(deleteEvent);

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Аккаунт удален"),
                eq("Здравствуйте, John Doe! Ваш аккаунт был удалён.")
        );
    }
}