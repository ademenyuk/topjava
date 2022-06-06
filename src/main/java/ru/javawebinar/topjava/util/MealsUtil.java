package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTo> mealsTo = filteredByCyclesOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreamsOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> eatenCaloriesPerDay = new HashMap<>();
        meals.forEach( meal ->
            eatenCaloriesPerDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));

        List<MealTo> mealsWithExcess = new ArrayList<>();
        meals.forEach(meal -> {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(new MealTo(meal,
                        isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)));
            }
        });
        return mealsWithExcess;
    }

    public static List<MealTo> filteredByCyclesOptional(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
       SortedSet<MealTo> mealsWithExcess = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(o -> o.getDateTime().toLocalDate())));
       Map<LocalDate, Integer> eatenCaloriesPerDay = new HashMap<>();

       meals.forEach(meal -> {
           if (addCaloriesAndCheckExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), meal.getCalories(), caloriesPerDay)) {
               excessOldMealsByDate(meal.getDateTime().toLocalDate(), mealsWithExcess);
            }
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcess.add(new MealTo(meal, isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)));
            }
       });
        return new ArrayList<>(mealsWithExcess);
    }

    private static boolean isDateExcess(Map<LocalDate, Integer> eatenCaloriesPerDay, LocalDate date, int caloriesPerDay) {
        if (!eatenCaloriesPerDay.containsKey(date)) {
            return false;
        }
        return eatenCaloriesPerDay.get(date) > caloriesPerDay;
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> eatenCaloriesPerDay = meals.stream().
                collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(),
                        Collectors.summingInt(Meal::getCalories)));

        return meals.stream().filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal ->
                        new MealTo(meal, isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)))
                .collect(Collectors.toList());

    }

    public static List<MealTo> filteredByStreamsOptional(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> eatenCaloriesPerDay = new HashMap<>();

        Collector<Meal, SortedSet<MealTo>, List<MealTo>> mealCollector =
                new Collector<Meal, SortedSet<MealTo>, List<MealTo>>() {
            @Override
            public Supplier<SortedSet<MealTo>> supplier() {
                return () -> Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(o -> o.getDateTime().toLocalDate())));
            }

            @Override
            public BiConsumer<SortedSet<MealTo>, Meal> accumulator() {
                return ((sortedSet, meal) -> {
                    if (addCaloriesAndCheckExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(),
                            meal.getCalories(), caloriesPerDay)) {
                        excessOldMealsByDate(meal.getDateTime().toLocalDate(), sortedSet);
                    }
                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        sortedSet.add(new MealTo(meal,
                                isDateExcess(eatenCaloriesPerDay, meal.getDateTime().toLocalDate(), caloriesPerDay)));
                    }
                });
            }

            @Override
            public BinaryOperator<SortedSet<MealTo>> combiner() {
                return ((sortedSet1, sortedSet2) -> {
                    sortedSet2.forEach(mealWithExcess -> {
                        if (addCaloriesAndCheckExcess(eatenCaloriesPerDay, mealWithExcess.getDateTime().toLocalDate(),
                                mealWithExcess.getCalories(), caloriesPerDay)) {
                            excessOldMealsByDate(mealWithExcess.getDateTime().toLocalDate(), sortedSet1);
                        }
                        if (TimeUtil.isBetweenHalfOpen(mealWithExcess.getDateTime().toLocalTime(), startTime, endTime)) {
                            sortedSet1.add(mealWithExcess);
                        }
                    });
                    return sortedSet1;
                });
            }

            @Override
            public Function<SortedSet<MealTo>, List<MealTo>> finisher() {
                return ArrayList::new;
            }

            @Override
            public Set<Characteristics> characteristics() {
                Set<Characteristics> characteristics = new HashSet<>();
                characteristics.add(Characteristics.CONCURRENT);
                characteristics.add(Characteristics.UNORDERED);
                return characteristics;
            }
        };

        return meals.stream().collect(mealCollector);

    }

    private static void excessOldMealsByDate(LocalDate mealDate, SortedSet<MealTo> meals) {

        SortedSet<MealTo> changingMeals = meals.subSet(MealTo.generateEmptyMealByDate(mealDate),
                MealTo.generateEmptyMealByDate(mealDate.plusDays(1)));

        for (MealTo meal : changingMeals) {
            meal.setExcess(true);
        }
    }

    private static boolean addCaloriesAndCheckExcess(Map<LocalDate, Integer> eatenCaloriesPerDay, LocalDate mealDate,
                                                     Integer calories, Integer maxCaloriesPerDay) {
        boolean excessBeforeAddingNewMeal = isDateExcess(eatenCaloriesPerDay, mealDate, maxCaloriesPerDay);
        eatenCaloriesPerDay.merge(mealDate, calories, Integer::sum);

        return !excessBeforeAddingNewMeal && isDateExcess(eatenCaloriesPerDay, mealDate, maxCaloriesPerDay);
    }

}
