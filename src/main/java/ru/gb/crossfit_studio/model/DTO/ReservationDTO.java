package ru.gb.crossfit_studio.model.DTO;

import lombok.Data;

@Data
public class ReservationDTO {
    private Long id;
    private Long userId;
    private Long trainingId;
}
