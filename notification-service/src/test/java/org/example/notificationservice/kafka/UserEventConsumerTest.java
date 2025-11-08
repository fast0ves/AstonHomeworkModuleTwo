package org.example.notificationservice.kafka;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("Добро пожаловать!", subjectCaptor.getValue());
        assertTrue(bodyCaptor.getValue().contains("John Doe"));
        assertTrue(bodyCaptor.getValue().contains("Ваш аккаунт на сайте ваш сайт был успешно создан"));
    }

    @Test
    void consumeUserEvent_DeleteOperation_SendsDeletionEmail() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(deleteEvent);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("Аккаунт удален", subjectCaptor.getValue());
        assertTrue(bodyCaptor.getValue().contains("John Doe"));
        assertTrue(bodyCaptor.getValue().contains("Ваш аккаунт был удалён"));
    }

    @Test
    void consumeUserEvent_UnknownOperation_DoesNotSendEmail() {
        UserEventDto unknownEvent = new UserEventDto("UNKNOWN", "test@example.com", "John Doe");

        userEventConsumer.consumeUserEvent(unknownEvent);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void consumeUserEvent_NullEvent_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> userEventConsumer.consumeUserEvent(null));
    }

    @Test
    void consumeUserEvent_CreateOperationWithNullUserName_SendsEmailWithNull() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", null);

        userEventConsumer.consumeUserEvent(createEvent);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(anyString(), anyString(), bodyCaptor.capture());

        assertTrue(bodyCaptor.getValue().contains("null"));
    }

    @Test
    void consumeUserEvent_DeleteOperationWithEmptyUserName_SendsEmailWithEmptyName() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "test@example.com", "");

        userEventConsumer.consumeUserEvent(deleteEvent);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(anyString(), anyString(), bodyCaptor.capture());

        assertTrue(bodyCaptor.getValue().contains("Здравствуйте, !"));
    }

    @Test
    void consumeUserEvent_WhenEmailServiceThrowsException_LogsError() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");
        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> userEventConsumer.consumeUserEvent(createEvent));

        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }
}