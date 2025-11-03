package org.example.kafka;

import org.example.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @InjectMocks
    private UserEventProducer userEventProducer;

    @Captor
    private ArgumentCaptor<UserEventDto> eventCaptor;

    private static final String TOPIC = "user-events";

    @Test
    void constructor_ShouldInitializeKafkaTemplate() {
        assertNotNull(userEventProducer);
    }

    @Test
    void sendUserCreatedEvent_ShouldSendCreateEventToKafka() {
        // Given
        String email = "test@example.com";
        String userName = "John Doe";

        // When
        userEventProducer.sendUserCreatedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("CREATE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals(userName, capturedEvent.getUserName());
    }

    @Test
    void sendUserCreatedEvent_WithNullUserName_ShouldSendEventWithNullUserName() {
        // Given
        String email = "test@example.com";
        String userName = null;

        // When
        userEventProducer.sendUserCreatedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("CREATE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertNull(capturedEvent.getUserName());
    }

    @Test
    void sendUserCreatedEvent_WithEmptyUserName_ShouldSendEventWithEmptyUserName() {
        // Given
        String email = "test@example.com";
        String userName = "";

        // When
        userEventProducer.sendUserCreatedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("CREATE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals("", capturedEvent.getUserName());
    }

    @Test
    void sendUserCreatedEvent_WithNullEmail_ShouldSendEventWithNullEmail() {
        // Given
        String email = null;
        String userName = "John Doe";

        // When
        userEventProducer.sendUserCreatedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("CREATE", capturedEvent.getOperation());
        assertNull(capturedEvent.getEmail());
        assertEquals(userName, capturedEvent.getUserName());
    }

    @Test
    void sendUserDeletedEvent_ShouldSendDeleteEventToKafka() {
        // Given
        String email = "test@example.com";
        String userName = "John Doe";

        // When
        userEventProducer.sendUserDeletedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("DELETE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals(userName, capturedEvent.getUserName());
    }

    @Test
    void sendUserDeletedEvent_WithNullUserName_ShouldSendEventWithNullUserName() {
        // Given
        String email = "test@example.com";
        String userName = null;

        // When
        userEventProducer.sendUserDeletedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("DELETE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertNull(capturedEvent.getUserName());
    }

    @Test
    void sendUserDeletedEvent_WithEmptyUserName_ShouldSendEventWithEmptyUserName() {
        // Given
        String email = "test@example.com";
        String userName = "";

        // When
        userEventProducer.sendUserDeletedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("DELETE", capturedEvent.getOperation());
        assertEquals(email, capturedEvent.getEmail());
        assertEquals("", capturedEvent.getUserName());
    }

    @Test
    void sendUserDeletedEvent_WithNullEmail_ShouldSendEventWithNullEmail() {
        // Given
        String email = null;
        String userName = "John Doe";

        // When
        userEventProducer.sendUserDeletedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());

        UserEventDto capturedEvent = eventCaptor.getValue();
        assertEquals("DELETE", capturedEvent.getOperation());
        assertNull(capturedEvent.getEmail());
        assertEquals(userName, capturedEvent.getUserName());
    }

    @Test
    void sendUserCreatedEvent_ShouldUseCorrectTopic() {
        // Given
        String email = "test@example.com";
        String userName = "John Doe";

        // When
        userEventProducer.sendUserCreatedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), any(UserEventDto.class));
    }

    @Test
    void sendUserDeletedEvent_ShouldUseCorrectTopic() {
        // Given
        String email = "test@example.com";
        String userName = "John Doe";

        // When
        userEventProducer.sendUserDeletedEvent(email, userName);

        // Then
        verify(kafkaTemplate).send(eq(TOPIC), any(UserEventDto.class));
    }

    @Test
    void bothMethods_ShouldCreateDifferentEventTypes() {
        // Given
        String email = "test@example.com";
        String userName = "John Doe";

        // When - send create event
        userEventProducer.sendUserCreatedEvent(email, userName);
        verify(kafkaTemplate).send(eq(TOPIC), eventCaptor.capture());
        UserEventDto createEvent = eventCaptor.getValue();

        // When - send delete event
        userEventProducer.sendUserDeletedEvent(email, userName);
        verify(kafkaTemplate, times(2)).send(eq(TOPIC), eventCaptor.capture());
        UserEventDto deleteEvent = eventCaptor.getValue();

        // Then
        assertEquals("CREATE", createEvent.getOperation());
        assertEquals("DELETE", deleteEvent.getOperation());
        // Email and userName should be the same in both events
        assertEquals(email, createEvent.getEmail());
        assertEquals(email, deleteEvent.getEmail());
        assertEquals(userName, createEvent.getUserName());
        assertEquals(userName, deleteEvent.getUserName());
    }
}