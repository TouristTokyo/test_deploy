package ru.vsu.cs.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.api.services.EmailService;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Подтверждение почты", description = "Метод для подтверждения почты")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send_email")
    @Operation(summary = "Отправка кода на указанный почтовый адрес (email)")
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email) {
        try {
            return new ResponseEntity<>(emailService.sendSimpleEmail(email), HttpStatus.OK);
        } catch (MailException mailException) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
