package org.example.notification.controller;

import org.example.dto.UserEventDto;
import org.example.notification.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody UserEventDto dto) {
        emailService.sendNotification(dto.getAction(), dto.getEmail());
        return ResponseEntity.ok("Письмо успешно отправлено на " + dto.getEmail());
    }
}
