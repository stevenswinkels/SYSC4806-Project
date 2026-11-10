package com.example.demo.services;


import com.example.demo.model.Professor;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.activation.MimeType;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MailSenderServiceTest {

    private String to = "example@example.org";
    private String subject = "test subject";
    private String text = "text message text";
    private String from = "test@example.org";
    private String link = "http://localhost:8080/";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "springboot"))
            .withPerMethodLifecycle(true);

    @Autowired
    private MailSenderService mailSenderService;

    @Mock   private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testSendMessage() throws Exception {

        mailSenderService.sendMail(to, subject, text);


        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            assertEquals(text, GreenMailUtil.getBody(receivedMessage));
            assertEquals(subject,  receivedMessage.getSubject());
            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals(from,  receivedMessage.getFrom()[0].toString());
            assertEquals(to, receivedMessage.getAllRecipients()[0].toString());
        });
    }

    @Test
    void testSendLinkEmail() throws Exception {
        mailSenderService.sendLink(to, subject, text, link);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            assertTrue(GreenMailUtil.getBody(receivedMessage).contains(text));
            assertTrue(GreenMailUtil.getBody(receivedMessage).contains(link));
            assertEquals(subject,  receivedMessage.getSubject());
            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals(from,  receivedMessage.getFrom()[0].toString());
            assertEquals(to, receivedMessage.getAllRecipients()[0].toString());
        });
    }

    @Test
    void testSendNotificationOfNewApplication() throws Exception {
        Professor prof = mock(Professor.class);
        when(prof.getId()).thenReturn(1L);
        when(prof.getName()).thenReturn("test prof");
        when(prof.getEmail()).thenReturn(to);

        mailSenderService.sendNotificationOfAssignment(prof);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            assertTrue(GreenMailUtil.getBody(receivedMessage).contains("/evaluations/1"));
            assertEquals("New Applications to Review",  receivedMessage.getSubject());
            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals(from,  receivedMessage.getFrom()[0].toString());
            assertEquals(to, receivedMessage.getAllRecipients()[0].toString());
        });

    }
}