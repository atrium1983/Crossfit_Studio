package ru.gb.crossfit_studio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.crossfit_studio.model.DTO.*;
import ru.gb.crossfit_studio.model.Training;
import ru.gb.crossfit_studio.service.TrainingService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/id/{id}")
    public ResponseEntity<TrainingInfoDTO> getTrainingInfoById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(trainingService.findTrainingInfoById(id));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Training>> getAll(){
        return ResponseEntity.ok(trainingService.findAll());
    }

    @GetMapping("/dates/{date}")
    public ResponseEntity<List<Training>> getAllByDate(@PathVariable LocalDate date){
        List<Training> trainings = trainingService.findAllByDate(date);
        if(!trainings.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(trainings);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all-by-user/{id}")
    public ResponseEntity<List<TrainingInfoDTO>> getAllTrainingsByUserId(@PathVariable Long id){
        try {
            return ResponseEntity.ok(trainingService.findAllTrainingsByUserId(id));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/user/{login}")
    public ResponseEntity<List<TrainingInfoDTO>> getAllTrainingsByLoginByUser(@PathVariable String login){
        try {
            return ResponseEntity.ok(trainingService.findAllTrainingsByUserLogin(login));
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Training> create(@RequestBody TrainingDTO trainingDTO){
        try {
            Training training = trainingService.create(trainingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(training);
        } catch (RuntimeException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Training> update(@PathVariable Long id, @RequestBody TrainingDTO trainingDTO){
        try {
            Training updatedTraining = trainingService.update(id, trainingDTO);
            return ResponseEntity.ok(updatedTraining);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/plan/{id}")
    public ResponseEntity<Training> updatePlan(@PathVariable Long id, @RequestBody ChangeTrainingPlanDTO plan){
        try {
            Training updatedTraining = trainingService.updateTrainingPlan(id, plan);
            return ResponseEntity.ok(updatedTraining);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/update/limit/{id}")
    public ResponseEntity<Training> updateLimit(@PathVariable Long id, @RequestBody ChangeLimitDTO limit){
        try {
            Training updatedTraining = trainingService.updateLimit(id, limit);
            return ResponseEntity.ok(updatedTraining);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/update/availability/{id}")
    public ResponseEntity<Training> updateLimit(@PathVariable Long id, @RequestBody ChangeAvailabilityDTO availability){
        try {
            Training updatedTraining = trainingService.updateAvailability(id, availability);
            return ResponseEntity.ok(updatedTraining);
        } catch (NoSuchElementException e){
            System.out.println(e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable Long id){
        try {
            trainingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

}
