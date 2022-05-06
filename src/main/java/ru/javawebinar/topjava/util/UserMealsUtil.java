package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        Map<LocalDate, Integer> eatenCaloriesPerDay = new HashMap<>();
        for (UserMeal meal : meals) {
            eatenCaloriesPerDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(new UserMealWithExcess(meal,
                        isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)));
            }
        }
        return mealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
       SortedSet<UserMealWithExcess> mealsWithExcess = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(o -> o.getDateTime().toLocalDate())));
       Map<LocalDate, Integer> eatenCaloriesByDate = new HashMap<>();

       for (UserMeal meal : meals) {

            LocalDate mealDate = meal.getDateTime().toLocalDate();
            boolean excessBeforeAddingNewMeal = isDateExcess(eatenCaloriesByDate, mealDate, caloriesPerDay);
            eatenCaloriesByDate.merge(mealDate, meal.getCalories(), Integer::sum);
            boolean excessAfterAddingNewMeal = isDateExcess(eatenCaloriesByDate, mealDate, caloriesPerDay);

           UserMealWithExcess newMeal = new UserMealWithExcess(meal, excessAfterAddingNewMeal);

           if (!excessBeforeAddingNewMeal && excessAfterAddingNewMeal) {
               UserMealWithExcess nextDayFictiveMeal = new UserMealWithExcess(meal.getDateTime().plusDays(1), "", 0, false);
               SortedSet<UserMealWithExcess> changingMeals = mealsWithExcess.subSet(newMeal, nextDayFictiveMeal);
               for (UserMealWithExcess mealWithExcess : changingMeals) {
                        mealWithExcess.setExcess(true);
                }
            }
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(newMeal);
            }
        }
        return new ArrayList<>(mealsWithExcess);
    }

    private static boolean isDateExcess(Map<LocalDate, Integer> eatenCaloriesPerDay, LocalDate date, int caloriesPerDay) {
        if (!eatenCaloriesPerDay.containsKey(date)) {
            return false;
        }
        return eatenCaloriesPerDay.get(date) > caloriesPerDay;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> eatenCaloriesPerDay = meals.stream().
                collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal ->
                        new UserMealWithExcess(meal, isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)))
                .collect(Collectors.toList());

    }

}
