package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealTo {
    private final LocalDateTime dateTime;

    private final String description;

    private int calories;

    private boolean excess;

    public MealTo(LocalDateTime dateTime, String description, int calories, boolean excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public MealTo(UserMeal meal, boolean excess) {
        this.dateTime = meal.getDateTime();
        this.description = meal.getDescription();
        this.calories = meal.getCalories();
        this.excess = excess;
    }

    public boolean isExcess() {
        return excess;
    }

    public void setExcess(boolean excess) {
        this.excess = excess;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }

    public static UserMealWithExcess generateEmptyMealByDate(LocalDate date) {
        return new UserMealWithExcess(date.atStartOfDay(), "", 0, false);
    }
}
