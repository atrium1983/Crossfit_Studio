package ru.gb.crossfit_studio.security;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
    private String role;
}
