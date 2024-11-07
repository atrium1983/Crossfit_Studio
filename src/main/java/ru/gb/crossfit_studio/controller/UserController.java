package ru.gb.crossfit_studio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.crossfit_studio.model.DTO.*;
import ru.gb.crossfit_studio.model.User;
import ru.gb.crossfit_studio.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        Optional<User> user = userService.findById(id);
        return user.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{login}")
    public ResponseEntity<User> getByLoginByCustomer(@PathVariable String login){
        return ResponseEntity.ok(userService.findByLogin(login));
    }

    @GetMapping("/{login}")
    public ResponseEntity<User> getByLogin(@PathVariable String login){
        return ResponseEntity.ok(userService.findByLogin(login));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/birthdays/{date}") // нужно для автоматической рассылки
    public ResponseEntity<List<User>> getAllWhoHasBirthday(@PathVariable LocalDate date){
        return ResponseEntity.ok(userService.findAllWhoHasBirthday(date));

    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserDTO userDTO){
        try {
            User updatedUser = userService.updateById(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/login/{login}")
    public ResponseEntity<User> updateLogin(@PathVariable String login, @RequestBody ChangeLoginDTO newLogin){
        try {
            User updatedUser = userService.updateLoginByLogin(login, newLogin);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/email/{login}")
    public ResponseEntity<User> updateEmail(@PathVariable String login, @RequestBody ChangeEmailDTO newEmail){
        try {
            User updatedUser = userService.updateEmailByLogin(login, newEmail);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/password/{login}")
    public ResponseEntity<User> updatePassword(@PathVariable String login, @RequestBody ChangePasswordDTO newPassword){
        try {
            User updatedUser = userService.updatePasswordByLogin(login, newPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDTO userDTO){
        try {
            User user = userService.create(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

}
