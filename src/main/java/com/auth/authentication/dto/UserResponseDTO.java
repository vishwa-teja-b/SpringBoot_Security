package com.auth.authentication.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private java.util.List<String> roles;
}
