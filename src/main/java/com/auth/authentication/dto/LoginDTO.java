package com.auth.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "username or email is mandatory")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
