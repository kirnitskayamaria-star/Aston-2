package org.example.notification.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.example.notification.dto.UserEventDto;
import org.example.notification.kafka.NotificationListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false"
})
public class NotificationServiceTests {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
        System.setProperty("spring.mail.host", "localhost");
        System.setProperty("spring.mail.port", "3025");
        System.setProperty("spring.mail.username", "");
        System.setProperty("spring.mail.password", "");
        System.setProperty("spring.mail.properties.mail.smtp.auth", "false");
        System.setProperty("spring.mail.properties.mail.smtp.starttls.enable", "false");
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
            .withPerMethodLifecycle(true);

    @Autowired
    private NotificationListener notificationListener;

    @Test
    @DisplayName("Проверка отправки письма при получении события CREATE")
    public void testKafkaNotificationOnCreate() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
        UserEventDto event = new UserEventDto("CREATE", "our_app@mail.com");
        notificationListener.listen(event);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        String recipient = java.util.Arrays.toString(receivedMessages[0].getAllRecipients());
        assertThat(recipient).contains("our_app@mail.com");
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Регистрация успешна");
    }

    @Test
    @DisplayName("Проверка отправки письма при получении события DELETE")
    public void testKafkaNotificationOnDelete() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
        UserEventDto event = new UserEventDto("DELETE", "our_app@mail.com");
        notificationListener.listen(event);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        String recipient = java.util.Arrays.toString(receivedMessages[0].getAllRecipients());
        assertThat(recipient).contains("our_app@mail.com");
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Удаление аккаунта");
    }
}
