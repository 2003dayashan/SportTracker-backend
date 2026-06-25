package com.example.SportsTracker.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document("password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;
    private String email;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
}