package ru.gb.crossfit_studio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long userId;
    private Long trainingId;
}
