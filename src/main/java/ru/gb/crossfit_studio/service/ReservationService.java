package ru.gb.crossfit_studio.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gb.crossfit_studio.email.DefaultEmailService;
import ru.gb.crossfit_studio.model.*;
import ru.gb.crossfit_studio.model.DTO.ReservationDTO;
import ru.gb.crossfit_studio.model.DTO.ReservationInfoDTO;
import ru.gb.crossfit_studio.model.DTO.ReservationShortInfoDTO;
import ru.gb.crossfit_studio.repository.UserRepository;
import ru.gb.crossfit_studio.repository.ReservationRepository;
import ru.gb.crossfit_studio.repository.TrainingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final DefaultEmailService emailService;

    public ReservationService(ReservationRepository reservationRepository, TrainingRepository trainingRepository, UserRepository userRepository, DefaultEmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Optional<Reservation> findById(Long id){
        return reservationRepository.findById(id);
    }

    public List<Reservation> findAll(){
        return reservationRepository.findAll();
    }

    public ReservationInfoDTO findReservationInfoById(Long id){
        ReservationShortInfoDTO reservationShortInfoDTO = findReservationShortInfoById(id);

        return new ReservationInfoDTO(reservationShortInfoDTO.getId()
                , reservationShortInfoDTO.getDate()
                , reservationShortInfoDTO.getTime()
                , findById(reservationShortInfoDTO.getId()).get().getUser().getFirstName()
                , findById(reservationShortInfoDTO.getId()).get().getUser().getLastName()
                , reservationShortInfoDTO.getTrainingId()
                , reservationShortInfoDTO.getStatus());
    }

    public ReservationShortInfoDTO findReservationShortInfoById(Long id){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));

        return new ReservationShortInfoDTO(reservation.getId()
                , reservation.getTraining().getDate()
                , reservation.getTraining().getTime()
                , reservation.getTraining().getId()
                , reservation.getStatus());
    }

    public ReservationInfoDTO findReservationInfoByIdByLogin(Long id, String login){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));
        if(reservation.getUser().getLogin().equals(login)){
            return findReservationInfoById(id);
        } else {
            throw new RuntimeException("Reservation with id " + id + " is not related to user with login + " + login);
        }
    }

    public ReservationInfoDTO findReservationInfoByIdAndUserId(Long id, Long userId){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));
        if(reservation.getUser().getId().equals(userId)){
            return findReservationInfoById(id);
        } else {
            throw new RuntimeException("Reservation with id " + id + " is not related to user with id + " + userId);
        }
    }

    public List<ReservationShortInfoDTO> findAllReservationsByLogin(String login){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        List<ReservationShortInfoDTO> reservationInfoDTOList = new ArrayList<>();
        List<Reservation> reservations = user.getReservations();
        for (Reservation reservation : reservations) {
            reservationInfoDTOList.add(findReservationShortInfoById(reservation.getId()));
        }

        return reservationInfoDTOList;
    }

    public List<ReservationShortInfoDTO> findAllReservationsByUserId(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " does not exist"));
        List<ReservationShortInfoDTO> reservationInfoDTOList = new ArrayList<>();
        List<Reservation> reservations = user.getReservations();
        for (Reservation reservation : reservations) {
            reservationInfoDTOList.add(findReservationShortInfoById(reservation.getId()));
        }

        return reservationInfoDTOList;
    }

    public Reservation create(ReservationDTO reservationData) {
        User user = findUserById(reservationData.getUserId());
        Training training = findTrainingById(reservationData.getTrainingId());
        if(training.isAvailable()){
            if(checkIfReservationExist(user, training)){
                throw new RuntimeException("User ready has reservation for the same training");
            } else if (checkUserTrainingDate(user, training)) {
                throw new RuntimeException("User ready has reservation for the same day");
            } else {
                Reservation reservation = new Reservation();
                reservation.setUser(user);
                reservation.setTraining(training);
                if(training.getReservations().size() < training.getReservationLimit()){
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    emailService.sendConfirmation(user.getEmail(),
                            user.getFirstName(),
                            training.getDate(),
                            training.getTime());
                } else {
                    reservation.setStatus(ReservationStatus.WAITING_LIST);
                    emailService.sendWaitingListConfirmation(user.getEmail(),
                            user.getFirstName(),
                            training.getDate(),
                            training.getTime());
                }
                return reservationRepository.save(reservation);
            }
        } else {
            throw new RuntimeException("Training is not available for reservation");
        }

    }

    public Reservation createByUser(ReservationDTO reservationDTO, String login){
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " does not exist"));
        reservationDTO.setUserId(user.getId());
        return create(reservationDTO);

    }

    public ReservationInfoDTO createByUserAndConfirm(ReservationDTO reservationDTO, String login){
        return findReservationInfoById(createByUser(reservationDTO, login).getId());
    }

    public ReservationInfoDTO createByAdminAndConfirm(ReservationDTO reservationDTO){
        return findReservationInfoById(create(reservationDTO).getId());
    }

    public Reservation updateReservationStatus(Long id){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));
        if(reservation.getStatus().equals(ReservationStatus.WAITING_LIST)){
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
            Training training = reservation.getTraining();
            training.setReservationLimit(training.getReservationLimit() + 1);
            trainingRepository.save(training);
            emailService.sendConfirmation(reservation.getUser().getEmail(),
                    reservation.getUser().getFirstName(),
                    training.getDate(),
                    training.getTime());
            return reservation;
        } else {
            throw new RuntimeException("Reservation with id " + id + " is ready confirmed");
        }
    }

    public ReservationInfoDTO confirmFromWaitingList(Long id){
        return findReservationInfoById(updateReservationStatus(id).getId());
    }

    public void deleteById(Long id){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));
        if(checkIfWaitingList(id) && !reservation.getStatus().equals(ReservationStatus.WAITING_LIST)){
            Long trainingId = reservation.getTraining().getId();
            reservationRepository.deleteById(id);
            confirmReservationOnWaiting(trainingId);
        }
        reservationRepository.deleteById(id);
        emailService.sendTrainingDeleteConfirmation(reservation.getUser().getEmail(),
                reservation.getUser().getFirstName(),
                reservation.getTraining().getDate(),
                reservation.getTraining().getTime());
    }

    public void deleteByIdByLogin(Long id, String login){
        Reservation reservation = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist"));
        if(reservation.getUser().getLogin().equals(login)){
            if(!reservation.getStatus().equals(ReservationStatus.EXECUTED)){
                deleteById(id);
            } else {
                throw new RuntimeException("Reservation with id " + id + " has status EXECUTED and can not be deleted");
            }
        } else {
            throw new RuntimeException("Reservation with id " + id + " is not related to user with login + " + login);
        }
    }

    public boolean checkIfReservationExist(User user, Training training){
        return user.getReservations().stream()
                .anyMatch(reservation -> reservation.getTraining().equals(training));
    }

    public boolean checkUserTrainingDate(User user, Training training){
        List<Training> trainings = new ArrayList<>();
        user.getReservations().forEach(reservation -> trainings.add(reservation.getTraining()));
        return trainings.stream()
                .anyMatch(t -> t.getDate().equals(training.getDate()));
    }

    public boolean checkIfWaitingList(Long reservationId){
        Reservation reservation = findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + reservationId + " does not exist"));
        return reservation.getTraining().getReservations().stream()
                .anyMatch(r -> r.getStatus().equals(ReservationStatus.WAITING_LIST));
    }

    public Training findTrainingById(Long id){
        return trainingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Training with id " + id + " does not exist"));
    }

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " does not exist"));
    }

    public void confirmReservationOnWaiting(Long trainingId){
        Training training = findTrainingById(trainingId);
        List<Reservation> reservations = training.getReservations();
        reservations.get(training.getReservationLimit()).setStatus(ReservationStatus.CONFIRMED);
        training.setReservations(reservations);
        trainingRepository.save(training);
    }

    public void confirmReservationOverLimit(Long reservationId){
        Reservation reservation = findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + reservationId + " does not exist"));
        if(reservation.getStatus().equals(ReservationStatus.WAITING_LIST)){
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
            emailService.sendConfirmation(
                    reservation.getUser().getEmail(),
                    reservation.getUser().getFirstName(),
                    reservation.getTraining().getDate(),
                    reservation.getTraining().getTime());
        } else {
            throw new RuntimeException("Reservation with id " + reservationId + "is not on waiting list");
        }
    }

    public List<Reservation> cleanWaitingList (List<Reservation> reservations){
        reservations.removeIf(reservation -> reservation.getStatus().equals(ReservationStatus.WAITING_LIST));
        return reservations;
    }

    @Scheduled(fixedDelay = 30000)
    public void doTraining(){
        List<Training> trainingsToday = trainingRepository.findAllByDate(LocalDate.now());
        for (Training training : trainingsToday) {
            if(training.isAvailable() && training.getTime().isBefore(LocalTime.now())){
                training.setAvailable(false);
                trainingRepository.save(training);

                List<Reservation> reservations = training.getReservations();
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus().equals(ReservationStatus.CONFIRMED)) {
                        reservation.setStatus(ReservationStatus.EXECUTED);
                        reservationRepository.save(reservation);
                    }

                    if (reservation.getStatus().equals(ReservationStatus.WAITING_LIST)) {
                        reservationRepository.deleteById(reservation.getId());
                    }
                }
                training.setReservations(cleanWaitingList(reservations));
                trainingRepository.save(training);
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void remindAboutTraining(){
        List<Training> trainingsToday = trainingRepository.findAllByDate(LocalDate.now());
        for (Training training : trainingsToday) {
            if(training.isAvailable() && training.getTime().isAfter(LocalTime.now().plusMinutes(59))
                    && training.getTime().isBefore(LocalTime.now().plusHours(1))) {
                for (Reservation reservation : training.getReservations()) {
                    if (reservation.getStatus().equals(ReservationStatus.CONFIRMED)) {
                        emailService.sendTrainingReminder(
                                reservation.getUser().getEmail(),
                                reservation.getUser().getFirstName(),
                                training.getTime());
                    }
                }
            }
        }
    }

}
