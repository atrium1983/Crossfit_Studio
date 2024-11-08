package ru.gb.crossfit_studio.service;

import org.springframework.stereotype.Service;
import ru.gb.crossfit_studio.model.*;
import ru.gb.crossfit_studio.model.DTO.*;
import ru.gb.crossfit_studio.repository.UserRepository;
import ru.gb.crossfit_studio.repository.TrainingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public TrainingService(TrainingRepository trainingRepository, UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }

    public Optional<Training> findById(Long id){
        return trainingRepository.findById(id);
    }

    public TrainingInfoDTO findTrainingInfoById(Long id){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        List<Reservation> reservations = training.getReservations();
        List<UserReservationDTO> users = new ArrayList<>();
        for (Reservation reservation : reservations) {
            users.add(new UserReservationDTO(
                    reservation.getId(),
                    reservation.getUser().getFirstName(),
                    reservation.getUser().getLastName(),
                    reservation.getStatus()));
        }

        TrainingInfoDTO trainingInfoDTO = new TrainingInfoDTO();
        trainingInfoDTO.setDate(training.getDate());
        trainingInfoDTO.setTime(training.getTime());
        trainingInfoDTO.setTrainingPlan(training.getTrainingPlan());
        trainingInfoDTO.setReservationLimit(training.getReservationLimit());
        trainingInfoDTO.setUsers(users);

        return trainingInfoDTO;
    }

    public List<Training> findAll(){
        return trainingRepository.findAll();
    }

    public List<Training> findAllByDate(LocalDate date){
        List<Training> trainings = trainingRepository.findAllByDate(date);
        trainings.sort(Comparator.comparing(Training::getTime));
        return trainings;
    }

    public List<TrainingInfoDTO> findAllTrainingsByUserId(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " does not exist"));
        List<Reservation> reservations = user.getReservations();
        List<TrainingInfoDTO> trainings = new ArrayList<>();
        for (Reservation reservation : reservations) {
            TrainingInfoDTO trainingInfoDTO = findTrainingInfoById(reservation.getTraining().getId());
            trainings.add(trainingInfoDTO);
        }
        return trainings;
    }
    public List<TrainingInfoDTO> findAllTrainingsByUserLogin(String login){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        List<Reservation> reservations = user.getReservations();
        List<TrainingInfoDTO> trainings = new ArrayList<>();
        for (Reservation reservation : reservations) {
            TrainingInfoDTO trainingInfoDTO = findTrainingInfoById(reservation.getTraining().getId());
            trainings.add(trainingInfoDTO);
        }
        return trainings;
    }

    public Training create(TrainingDTO trainingDTO){
        if(checkIfTrainingIsExist(trainingDTO)){
            throw new RuntimeException("Training on this date and time is ready exist");
        } else {
            Training training = new Training();
            training.setDate(trainingDTO.getDate());
            training.setTime(trainingDTO.getTime());
            training.setTrainingPlan(trainingDTO.getTrainingPlan());
            training.setReservationLimit(trainingDTO.getReservationLimit());
            training.setAvailable(trainingDTO.isAvailable());
            return trainingRepository.save(training);
        }
    }

    public Training update(Long id, TrainingDTO newTrainingData){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        training.setDate(newTrainingData.getDate());
        training.setTime(newTrainingData.getTime());
        training.setTrainingPlan(newTrainingData.getTrainingPlan());

        return trainingRepository.save(training);
    }

    public Training updateTrainingPlan(Long id, ChangeTrainingPlanDTO plan){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        if(training.isAvailable()){
            training.setTrainingPlan(plan.getTrainingPlan());
        } else {
            throw new RuntimeException("Training with id " + id + " is ready not available");
        }

        return trainingRepository.save(training);
    }

    public Training updateLimit(Long id, ChangeLimitDTO limit){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        if(training.isAvailable()){
            training.setReservationLimit(limit.getReservationLimit());
        } else {
            throw new RuntimeException("Training with id " + id + " is ready not available");
        }

        return trainingRepository.save(training);
    }

    public Training updateAvailability(Long id, ChangeAvailabilityDTO availability){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        if(!checkIfTrainingIsPassed(id)){
            training.setAvailable(availability.isAvailable());
            return trainingRepository.save(training);
        } else {
            throw new RuntimeException("Training with id " + id + " is ready passed");
        }

    }

    public void delete(Long id){
        if(findById(id).isPresent()){
            trainingRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Training with id " + id + " does not exist");
        }

    }

    public boolean checkIfTrainingIsExist(TrainingDTO trainingData){
        List<Training> trainingsOnDate = findAllByDate(trainingData.getDate());
        return trainingsOnDate.stream().anyMatch(training -> training.getTime().equals(trainingData.getTime()));
    }

    public boolean checkIfTrainingIsPassed(Long id){
        Training training = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
        if(training.getDate().isAfter(LocalDate.now())
                || (Objects.equals(training.getDate(), LocalDate.now()) && training.getTime().isAfter(LocalTime.now()))){
            return false;
        }
        return true;
    }

}
