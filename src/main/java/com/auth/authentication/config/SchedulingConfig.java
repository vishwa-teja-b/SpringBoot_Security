package com.auth.authentication.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.auth.authentication.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;

@Component
@EnableScheduling
public class SchedulingConfig {
    private final PasswordResetTokenRepository tokenRepository;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SchedulingConfig.class);

    public SchedulingConfig(PasswordResetTokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 */15 * * * *") // Every 15 minutes
    @Transactional
    public void cleanUpExpiredTokens(){
        try{
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = tokenRepository.deleteExpiredOrUsedTokens(now);
            logger.info("Cleaned up {} expired or used tokens", deletedCount);
        }
        catch(Exception e){
            logger.error("Error during cleanup of expired tokens: {}", e.getMessage());
        }
    }
}
