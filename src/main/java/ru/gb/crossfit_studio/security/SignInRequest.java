package ru.gb.crossfit_studio.security;

import lombok.Data;

@Data
public class SignInRequest {
    private String login;
    private String password;
}
