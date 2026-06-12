package org.example.notification.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.example.dto.UserEventDto;
import org.example.notification.kafka.NotificationListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    private TestRestTemplate restTemplate;

    @MockBean
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @SpyBean
    private NotificationListener notificationListener;

    @Test
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
    public void testRestApiNotificationOnDelete() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();

        UserEventDto dto = new UserEventDto("DELETE", "our_app@mail.com");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/notifications/send", dto, String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);

        String recipient = java.util.Arrays.toString(receivedMessages[0].getAllRecipients());
        assertThat(recipient).contains("our_app@mail.com");
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Удаление аккаунта");
    }
}