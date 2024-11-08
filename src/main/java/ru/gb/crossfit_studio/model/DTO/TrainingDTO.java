package ru.gb.crossfit_studio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TrainingDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String trainingPlan;
    private int reservationLimit;
    private boolean available;
}
