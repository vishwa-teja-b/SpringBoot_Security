package com.auth.authentication.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
