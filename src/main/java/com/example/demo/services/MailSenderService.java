package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    @Autowired
    private MailSender mailSender;

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


    /*
    * Uncomment to test sending mail by just starting the app.
    * useful for testing smtp config.
    * */
//    @EventListener(ApplicationStartedEvent.class)
//    public void sendMail(){
//        sendMail(from,"test","test");
//    }

}