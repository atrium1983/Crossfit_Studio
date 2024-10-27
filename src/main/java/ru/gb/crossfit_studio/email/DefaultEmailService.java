package ru.gb.crossfit_studio.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class DefaultEmailService {
    private final JavaMailSender mailSender;
    public DefaultEmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendConfirmation(String email, String name, LocalDate date, LocalTime time){
        String subject = "Подтверждение тренировки";
        String body = "Привет, " + name + "! Твоя тренировка " + date + " в " + time + " подтверждена!";
        sendEmail(email, subject, body);
    }

    public void sendWaitingListConfirmation(String email, String name, LocalDate date, LocalTime time){
        String subject = "Тренировка в листе ожидания";
        String body = "Привет, " + name + "! На тренировке " + date + " в " + time + " сейчас нет мест, " +
                "но мы поставили тебя в лист ожидания. Если твоё бронирование будет подтверждено, " +
                "мы отправим тебе сообщение. ";
        sendEmail(email, subject, body);
    }

    public void sendTrainingReminder (String email, String name, LocalTime time){
        String subject = "Напоминание о тренировке";
        String body = "Привет, " + name + "! Твоя тренировка в " + time + " начнется через 1 час! Ждем тебя!";
        sendEmail(email, subject, body);
    }

    public void sendTrainingDeleteConfirmation(String email, String name, LocalDate date, LocalTime time) {
        String subject = "Отмена тренировки";
        String body = "Привет, " + name + "! Твоя тренировка " + date + " в " + time + " отменена.";
        sendEmail(email, subject, body);
    }

    public void sendEmailChangeConfirmation(String email, String name) {
        String subject = "Подтверждение нового адреса электронной почты";
        String body = "Привет, " + name + "! Твой адрес электронной почты был успешно изменен.";
        sendEmail(email, subject, body);
    }

    public void sendPasswordChangeConfirmation(String email, String name) {
        String subject = "Подтверждение нового пароля";
        String body = "Привет, " + name + "! Твой пароль был успешно изменен.";
        sendEmail(email, subject, body);
    }

    public void sendEmail(String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
