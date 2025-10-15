package com.auth.authentication.dto;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String email;
    private String pin;
    private String newPassword;
}
