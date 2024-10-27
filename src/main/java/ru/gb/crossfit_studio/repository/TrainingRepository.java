package ru.gb.crossfit_studio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.crossfit_studio.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByDate(LocalDate date);
}
