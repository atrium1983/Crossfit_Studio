package ru.gb.crossfit_studio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gb.crossfit_studio.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfoDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String firstName;
    private String lastName;
    private Long trainingId;
    private ReservationStatus status;
}
