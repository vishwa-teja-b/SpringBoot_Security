package com.auth.authentication.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.auth.authentication.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndPinAndUsedFalse(String email, String pin);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1 OR t.used = true")
    int deleteExpiredOrUsedTokens(LocalDateTime now);
    
}
