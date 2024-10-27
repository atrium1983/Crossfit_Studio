package ru.gb.crossfit_studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.gb.crossfit_studio.db_generator.UserGenerator;
import ru.gb.crossfit_studio.db_generator.ReservationGenerator;
import ru.gb.crossfit_studio.db_generator.TrainingGenerator;
import ru.gb.crossfit_studio.model.Role;
import ru.gb.crossfit_studio.repository.UserRepository;
import ru.gb.crossfit_studio.repository.ReservationRepository;
import ru.gb.crossfit_studio.repository.TrainingRepository;

import java.time.LocalDate;

@SpringBootApplication
public class CrossfitStudioApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(CrossfitStudioApplication.class, args);

		TrainingRepository trainingRepository = ctx.getBean(TrainingRepository.class);
		TrainingGenerator trainingGenerator = new TrainingGenerator();
		trainingGenerator.generateTrainingsInRepository(trainingRepository, LocalDate.now().minusDays(30),32);

		UserRepository userRepository = ctx.getBean(UserRepository.class);
		UserGenerator userGenerator = new UserGenerator();
		userGenerator.generateUsersInRepository(userRepository, Role.CUSTOMER,20);

		ReservationRepository reservationRepository = ctx.getBean(ReservationRepository.class);
		ReservationGenerator reservationGenerator = new ReservationGenerator();
		reservationGenerator.generateReservationsInRepository(reservationRepository, userRepository, trainingRepository, 350);

		userGenerator.generateUsersInRepository(userRepository, Role.TRAINER,4);

		userGenerator.generateUsersInRepository(userRepository, Role.ADMIN,2);
	}

}
