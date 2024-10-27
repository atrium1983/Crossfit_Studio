package ru.gb.crossfit_studio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gb.crossfit_studio.model.ReservationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReservationDTO {
    private Long reservationId;
    private String firstName;
    private String lastName;
    private ReservationStatus status;
}
