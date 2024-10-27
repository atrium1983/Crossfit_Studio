package ru.gb.crossfit_studio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.crossfit_studio.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByDateOfBirth(LocalDate date);
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);

}
