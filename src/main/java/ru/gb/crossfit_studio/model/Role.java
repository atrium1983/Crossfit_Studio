package ru.gb.crossfit_studio.model;

public enum Role {
    ADMIN("admin"), CUSTOMER("customer"), TRAINER("trainer");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
