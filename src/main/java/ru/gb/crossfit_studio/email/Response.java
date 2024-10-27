package ru.gb.crossfit_studio.email;

import java.time.LocalDate;
import java.time.LocalTime;

public class Response {

    public String getTrainingConfirmation(String name, LocalDate date, LocalTime time){
        return "Привет, " + name + "! Твоя тренировка " + date + " в " + time + " подтверждена!";
    }

    public String getTrainingWaitingListConfirmation(String name, LocalDate date, LocalTime time){
        return "Привет, " + name + "! На тренировке " + date + " в " + time + " сейчас нет мест, " +
                "но мы поставили тебя в лист ожидания. Если твоё бронирование будет подтверждено, " +
                "мы отправим тебе сообщение. ";
    }

    public String getTrainingReminder (String name, LocalTime time){
        return "Привет, " + name + "! Твоя тренировка в " + time + " начнется через 1 час! Ждем тебя!";
    }

    public String getTrainingDeleteConfirmation(String name, LocalDate date, LocalTime time) {
        return "Привет, " + name + "! Твоя тренировка " + date + " в " + time + " отменена.";
    }

}
