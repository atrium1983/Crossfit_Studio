package ru.gb.crossfit_studio.db_generator;

import ru.gb.crossfit_studio.model.User;
import ru.gb.crossfit_studio.model.Reservation;
import ru.gb.crossfit_studio.model.ReservationStatus;
import ru.gb.crossfit_studio.model.Training;
import ru.gb.crossfit_studio.repository.UserRepository;
import ru.gb.crossfit_studio.repository.ReservationRepository;
import ru.gb.crossfit_studio.repository.TrainingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReservationGenerator {
    public Reservation generateReservation(User user, Training training){
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTraining(training);
        if(training.getDate().isBefore(LocalDate.now())){
            reservation.setStatus(ReservationStatus.EXECUTED);
        } else {
            if (reservation.getTraining().getReservations().size() <= reservation.getTraining().getReservationLimit()) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
            } else {
                reservation.setStatus(ReservationStatus.WAITING_LIST);
            }
        }
        return reservation;
    }

    public int getRandomInt(int bound){
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public void generateReservationsInRepository(ReservationRepository reservationRepository,
                                                 UserRepository userRepository,
                                                 TrainingRepository trainingRepository,
                                                 int number){
        for (int i = 0; i < number; i++) {
            Training training;
            User user = userRepository.findAll().get(getRandomInt(userRepository.findAll().size()));

            do{
                training = trainingRepository.findAll().get(getRandomInt(trainingRepository.findAll().size()));
            } while (checkCustomerTrainingDate(user, training.getDate()));

            Reservation reservation = generateReservation(user, training);
            reservationRepository.save(reservation);

            user.getReservations().add(reservation);
            training.getReservations().add(reservation);
            userRepository.save(user);
            trainingRepository.save(training);
        }
    }

    public List<Training> getListOfAllUserTraining(User user){
        List<Training> trainings = new ArrayList<>();
        user.getReservations().forEach(reservation -> trainings.add(reservation.getTraining()));
        return trainings;
    }

    // метод проверяет есть ли у Customer бронирование на ту же дату, на которую он хочет забронировать
    public boolean checkCustomerTrainingDate(User user, LocalDate date){
        List<Training> trainings = getListOfAllUserTraining(user);
        return trainings.stream()
                .anyMatch(training -> training.getDate().equals(date));
    }
}
