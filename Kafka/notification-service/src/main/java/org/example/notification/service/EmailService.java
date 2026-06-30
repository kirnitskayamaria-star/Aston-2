package org.example.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(String action, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);

        if ("CREATE".equalsIgnoreCase(action)) {
            message.setSubject("Регистрация успешна");
            message.setText("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
        } else if ("DELETE".equalsIgnoreCase(action)) {
            message.setSubject("Удаление аккаунта");
            message.setText("Здравствуйте! Ваш аккаунт был удалён.");
        } else {
            throw new IllegalArgumentException("Неизвестная операция: " + action);
        }

        mailSender.send(message);
    }
}
