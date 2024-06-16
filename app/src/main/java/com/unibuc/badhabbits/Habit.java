package com.unibuc.badhabbits;

import java.time.LocalDate;

public class Habit {
    private String name;
    private LocalDate startDate;

    public Habit(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }
    public void  setName(String name) {this.name = name;}

    public LocalDate getStartDate() {
        return startDate;
    }

    public long getDaysCount() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }
}
