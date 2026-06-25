package com.example.SportsTracker.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Stick League — Password Reset");
        message.setText(
                "Hi there,\n\n" +
                        "You requested a password reset for your Stick League account.\n\n" +
                        "Click the link below to reset your password (valid for 30 minutes):\n\n" +
                        resetLink + "\n\n" +
                        "If you didn't request this, ignore this email.\n\n" +
                        "— Stick League Team"
        );

        mailSender.send(message);
    }
}