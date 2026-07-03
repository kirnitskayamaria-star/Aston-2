package org.example.user.service;

import org.example.user.dto.UserEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
    private static final String TOPIC = "user-notification-topic";

    public UserEventPublisher(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String action, String email) {
        kafkaTemplate.send(TOPIC, new UserEventDto(action, email));
    }
}
