package ru.gb.crossfit_studio.db_generator;

import ru.gb.crossfit_studio.model.Training;
import ru.gb.crossfit_studio.repository.TrainingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

public class TrainingGenerator {
    private final LocalTime[] trainingHour = new LocalTime[]{
            LocalTime.of(17, 00),
            LocalTime.of(18, 00),
            LocalTime.of(19, 00),
            LocalTime.of(20, 00),
            LocalTime.of(21, 00)
    };

    private final String[] trainingPlan = new String[]{
            "Спина", "Грудь", "Ноги", "Плечи", "Руки"
    };

    public Training generateTraining(LocalDate date, LocalTime time){
        Training training = new Training();
        training.setDate(date);
        training.setTime(time);
        training.setTrainingPlan(trainingPlan[getRandomInt(trainingPlan.length)]);
       if(date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))){
           training.setAvailable(false);
       } else {
           training.setAvailable(true);
       }

        return training;
    }

    public int getRandomInt(int bound){
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public void generateTrainingsInRepository(TrainingRepository trainingRepository, LocalDate startDate, int days){
        for (int i = 0; i < days; i++) {
            for (LocalTime time : trainingHour) {
                trainingRepository.save(generateTraining(startDate.plusDays(i), time));
            }
        }
    }

}
