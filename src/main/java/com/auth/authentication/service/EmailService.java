package com.auth.authentication.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender; // DEPENDENCY / INTERFACE THAT SENDS EMAILS
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /* @RequiredArgsConstructor - Lombok annotation that generates constructors for all final or @NonNull fields*/
    public void sendResetPasswordEmail(String to, String pin){
        try{
            logger.info("Preparing to send email to {}", to);
            SimpleMailMessage message = new SimpleMailMessage(); // SIMPLE MAIL MESSAGE IS A HELPER CLASS FOR CREATING A SIMPLE TEXT EMAIL
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("Your password reset PIN is : " +
                            pin + "\n This PIN will expire in 15 minutes.");
            mailSender.send(message);

            logger.info("Email sent successfully to {}", to);
        }
        catch(Exception e){
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            e.printStackTrace();
        }
    }

    // TASKS -->  LEARN SYNCHRONOUS AND ASYNCHRONOUS METODS OF SENDING EMAILS
}
