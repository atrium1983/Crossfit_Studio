package ru.gb.crossfit_studio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.crossfit_studio.model.DTO.ReservationInfoDTO;
import ru.gb.crossfit_studio.model.DTO.ReservationShortInfoDTO;
import ru.gb.crossfit_studio.model.Reservation;
import ru.gb.crossfit_studio.model.DTO.ReservationDTO;
import ru.gb.crossfit_studio.service.ReservationService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAll(){
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationInfoDTO> getReservationInfoById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(reservationService.findReservationInfoById(id));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{login}/id/{id}")
    public ResponseEntity<ReservationInfoDTO> getReservationInfoByIdByLogin(@PathVariable String login, @PathVariable Long id){
        try {
            return ResponseEntity.ok(reservationService.findReservationInfoByIdByLogin(id, login));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/all-by-user/{id}")
    public ResponseEntity<List<ReservationShortInfoDTO>> getAllReservationsByUserId(@PathVariable Long id){
        try {
            return ResponseEntity.ok(reservationService.findAllReservationsByUserId(id));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{login}")
    public ResponseEntity<List<ReservationShortInfoDTO>> getAllReservationsByLogin(@PathVariable String login){
        try {
            return ResponseEntity.ok(reservationService.findAllReservationsByLogin(login));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ReservationInfoDTO> create(@RequestBody ReservationDTO reservationDTO){
        try {
            ReservationInfoDTO reservation = reservationService.createByAdminAndConfirm(reservationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/user/{login}")
    public ResponseEntity<ReservationInfoDTO> createByUser(@RequestBody ReservationDTO reservationDTO, @PathVariable String login){
        try {
            ReservationInfoDTO reservation = reservationService.createByUserAndConfirm(reservationDTO, login);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationInfoDTO> confirmReservationFromWaitingList(@PathVariable Long id){
        try {
            ReservationInfoDTO reservationInfoDTO = reservationService.confirmFromWaitingList(id);
            return ResponseEntity.ok(reservationInfoDTO);
        } catch (NoSuchElementException e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try{
            reservationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{login}/id/{id}")
    public ResponseEntity<Void> deleteByIdByLogin(@PathVariable String login, @PathVariable Long id) {
        try {
            reservationService.deleteByIdByLogin(id, login);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
