package ru.gb.crossfit_studio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gb.crossfit_studio.email.DefaultEmailService;
import ru.gb.crossfit_studio.model.*;
import ru.gb.crossfit_studio.model.DTO.*;
import ru.gb.crossfit_studio.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DefaultEmailService emailService;

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public User findByLogin(String login){
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User with email " + email + " does not exist"));
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findAllWhoHasBirthday(LocalDate date){
        List<User> users = userRepository.findAll();
        List<User> usersWithBirthday = new ArrayList<>();
        for (User user : users) {
            if (user.getDateOfBirth().getDayOfMonth() == date.getDayOfMonth()
                    & user.getDateOfBirth().getMonthValue() == date.getMonthValue()){
                usersWithBirthday.add(user);
            }
        }
        return usersWithBirthday;
    }

    public User updateById(Long id, UserDTO newUserData){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " does not exist"));
        user.setFirstName(newUserData.getFirstName());
        user.setLastName(newUserData.getLastName());
        user.setDateOfBirth(newUserData.getDateOfBirth());
        user.setEmail(newUserData.getEmail());
        user.setLogin(newUserData.getLogin());
        user.setPassword(passwordEncoder().encode(newUserData.getPassword()));
        user.setRole(Role.valueOf(newUserData.getRole()));

        return userRepository.save(user);
    }

    public User updateLoginByLogin(String login, ChangeLoginDTO newLogin){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        user.setLogin(newLogin.getLogin());
        emailService.sendLoginChangeConfirmation(user.getEmail(), user.getFirstName());

        return userRepository.save(user);
    }

    public User updateEmailByLogin(String login, ChangeEmailDTO newEmail){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        user.setEmail(newEmail.getEmail());
        emailService.sendEmailChangeConfirmation(user.getEmail(), user.getFirstName());

        return userRepository.save(user);
    }

    public User updatePasswordByLogin(String login, ChangePasswordDTO newPassword){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        user.setPassword(passwordEncoder().encode(newPassword.getPassword()));
        emailService.sendPasswordChangeConfirmation(user.getEmail(), user.getFirstName());

        return userRepository.save(user);
    }

    public User create(UserDTO userData){
        if(checkIfUserExist(userData)){
            throw new RuntimeException("User who has the same date of birth, first and last name is ready exist");
        } else {
            User user = new User();
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setDateOfBirth(userData.getDateOfBirth());
            user.setEmail(userData.getEmail());
            user.setLogin(userData.getLogin());
            user.setPassword(passwordEncoder().encode(userData.getPassword()));
            user.setRole(Role.valueOf(userData.getRole()));
            emailService.sendProfileCreationConfirmation(userData.getEmail(), user.getFirstName());

            return userRepository.save(user);
        }
    }

    public void delete(Long id){
        if(findById(id).isPresent()){
            userRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("User with id " + id + " does not exist");
        }
    }

    public boolean checkIfUserExist(UserDTO userDTO){
        List<User> usersByBirthday = userRepository.findAllByDateOfBirth(userDTO.getDateOfBirth());
        return usersByBirthday.stream()
                .anyMatch(user -> user.getLastName().equals(userDTO.getLastName())
                        &user.getFirstName().equals(userDTO.getFirstName()));
    }

    public UserDetailsService userDetailsService() {
        return this::findByLogin;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var login = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByLogin(login);
    }
}
