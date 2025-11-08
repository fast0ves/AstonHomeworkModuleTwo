package org.example.notificationservice.integration;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Test
    void whenCreateEventSent_thenEmailServiceCalled() {
        UserEventDto createEvent = new UserEventDto("CREATE", "test@example.com", "John Doe");

        kafkaTemplate.send("user-events", createEvent);
        kafkaTemplate.flush();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
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
        kafkaTemplate.flush();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
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
        kafkaTemplate.flush();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
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
        kafkaTemplate.flush();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
        });

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}