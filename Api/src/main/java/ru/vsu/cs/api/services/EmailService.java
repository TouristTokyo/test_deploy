package ru.vsu.cs.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class EmailService {


    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.emailSender = sender;
    }

    public String sendSimpleEmail(String toAddress) {
        String message = generateCode();
        String subject = "Code";


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);

        log.info("Code sent successfully: " + message);

        return message;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
