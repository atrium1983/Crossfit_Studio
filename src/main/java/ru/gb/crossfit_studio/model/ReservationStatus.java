package ru.gb.crossfit_studio.model;

public enum ReservationStatus {
    CONFIRMED("confirmed"), WAITING_LIST("waitingList"), EXECUTED("executed");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
