package ru.gb.crossfit_studio.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Класс описывающий конкретную тренировку (каждый день с 17:00 до 22:00, одна тренировка - 1 час)
@Data
@Entity
@Table(name = "training")
public class Training {

//    public Training(){
//
//    }
//
//    @JsonCreator
//    public Training(LocalDate date, LocalTime time, String trainingPlan) {
//        this.date = date;
//        this.time = time;
//        this.trainingPlan = trainingPlan;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private LocalDate date;
    private LocalTime time;
    private String trainingPlan;
//    @OneToMany(mappedBy="training", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy="training", fetch = FetchType.EAGER, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonManagedReference
//    private Set<Reservation> reservations = new HashSet<>();
//    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

//    @Column(columnDefinition = "integer default 5")
    private int reservationLimit = 5;
//    @ManyToMany(mappedBy="trainingsInWaitingList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    private List<Customer> customersInWaitingList = new ArrayList<>();

    private boolean available;

}
