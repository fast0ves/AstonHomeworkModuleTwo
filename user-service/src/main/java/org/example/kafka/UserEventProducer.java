package org.example.kafka;

import org.example.dto.UserEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public UserEventProducer(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(String email, String userName) {
        UserEventDto event = new UserEventDto("CREATE", email, userName);
        kafkaTemplate.send(TOPIC, event);
    }


    public void sendUserDeletedEvent(String email, String userName) {
        UserEventDto event = new UserEventDto("DELETE", email, userName);
        kafkaTemplate.send(TOPIC, event);
    }
}
