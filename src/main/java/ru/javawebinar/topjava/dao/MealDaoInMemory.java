package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDaoInMemory implements MealDao {

    private AtomicInteger idCounter;
    private List<Meal> meals;

    public MealDaoInMemory() {
        this.meals = new CopyOnWriteArrayList<>();
        this.idCounter = new AtomicInteger(0);

        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        addMeal(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public void addMeal(Meal meal) {
        meal.setId(idCounter.incrementAndGet());
        meals.add(meal);
    }

    @Override
    public void deleteMeal(int mealId) {
        Meal mealById = getMealById(mealId);
        if (mealById != null) {
            meals.remove(mealById);
        }
    }

    @Override
    public void updateMeal(Meal meal) {
        Meal mealFromCollection = getMealById(meal.getId());
        if (mealFromCollection != null) {
           meals.remove(mealFromCollection);
           meals.add(meal);
        }
        else {
            addMeal(meal);
        }
    }

    @Override
    public List<Meal> getAllMeals() {
        return meals;
    }

    @Override
    public Meal getMealById(int mealId) {
        for (Meal meal : meals) {
            if (meal.getId().equals(mealId)) {
                return meal;
            }
        }
        return null;
    }
}
