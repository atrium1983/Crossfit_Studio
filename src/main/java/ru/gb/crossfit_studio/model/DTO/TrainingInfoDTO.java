package ru.gb.crossfit_studio.model.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TrainingInfoDTO {
    private LocalDate date;
    private LocalTime time;
    private String trainingPlan;
    private int reservationLimit;
    private List<UserReservationDTO> users;
}
