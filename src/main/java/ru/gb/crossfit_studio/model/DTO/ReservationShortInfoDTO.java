package ru.gb.crossfit_studio.model.DTO;

import lombok.Data;
import ru.gb.crossfit_studio.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationShortInfoDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private ReservationStatus status;

}
