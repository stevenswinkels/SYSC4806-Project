package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;


    @Value("${custom.mail.from}")
    private String from;

    public  void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    private void sendHTML(String to, String subject, String html){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true indicates HTML
            helper.setFrom(from);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }

    }

    public void sendLink(String to, String subject, String msg, String link) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("msg", msg);

        String htmlContent = templateEngine.process("link-email", context);

        sendHTML(to, subject, htmlContent);
    }


    /*
    * Uncomment to test sending mail by just starting the app.
    * useful for testing smtp config.
    * */
//    @EventListener(ApplicationStartedEvent.class)
//    public void sendMail(){
//        sendMail(from,"test","test");
//    }

}