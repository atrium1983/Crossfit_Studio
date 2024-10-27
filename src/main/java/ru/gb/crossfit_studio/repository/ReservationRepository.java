package ru.gb.crossfit_studio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.crossfit_studio.model.User;
import ru.gb.crossfit_studio.model.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);
}
