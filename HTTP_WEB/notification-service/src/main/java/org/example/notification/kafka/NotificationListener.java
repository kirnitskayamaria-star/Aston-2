package org.example.notification.kafka;

import org.example.notification.dto.UserEventDto;
import org.example.notification.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final EmailService emailService;

    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-notification-topic", groupId = "notification-group")
    public void listen(UserEventDto event) {
        if (event != null && event.getEmail() != null && event.getAction() != null) {
            emailService.sendNotification(event.getAction(), event.getEmail());
        }
    }
}
