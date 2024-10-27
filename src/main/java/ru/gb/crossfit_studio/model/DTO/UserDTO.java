package ru.gb.crossfit_studio.model.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String login;
    private String email;
    private String password;
    private String role;
}
