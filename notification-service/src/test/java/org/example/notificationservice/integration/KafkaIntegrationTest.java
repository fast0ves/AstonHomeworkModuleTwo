package org.example.notificationservice.integration;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "port=0"
        }
)
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Test
    void whenCreateEventSent_thenEmailServiceCalled() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");

        kafkaTemplate.send("user-events", createEvent);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService).sendEmail(
                    eq("test@example.com"),
                    eq("Добро пожаловать!"),
                    contains("John Doe")
            );
        });
    }

    @Test
    void whenDeleteEventSent_thenEmailServiceCalled() {
        UserEventDto deleteEvent = new UserEventDto("DELETE", "deleted@example.com", "Jane Smith");

        kafkaTemplate.send("user-events", deleteEvent);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService).sendEmail(
                    eq("deleted@example.com"),
                    eq("Аккаунт удален"),
                    contains("Jane Smith")
            );
        });
    }

    @Test
    void whenMultipleEventsSent_thenConsumerCalledMultipleTimes() {
        UserEventDto event1 = new UserEventDto("CREATE", "test1@example.com", "User One");
        UserEventDto event2 = new UserEventDto("DELETE", "test2@example.com", "User Two");

        kafkaTemplate.send("user-events", event1);
        kafkaTemplate.send("user-events", event2);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());

            verify(emailService).sendEmail(
                    eq("test1@example.com"),
                    eq("Добро пожаловать!"),
                    contains("User One")
            );

            verify(emailService).sendEmail(
                    eq("test2@example.com"),
                    eq("Аккаунт удален"),
                    contains("User Two")
            );
        });
    }

    @Test
    void whenUnknownEventSent_thenNoEmailSent() {
        UserEventDto unknownEvent = new UserEventDto("UNKNOWN", "test@example.com", "John Doe");

        kafkaTemplate.send("user-events", unknownEvent);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        });
    }
}