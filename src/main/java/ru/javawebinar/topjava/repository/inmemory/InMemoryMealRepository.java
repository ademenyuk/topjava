package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, SecurityUtil.authUserId()));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.get(userId).put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null || userMeals.get(meal.getId()) == null) {
            return null;
        }
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, Integer userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int id, Integer userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(Integer userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ?
                userMeals.values().stream().sorted(new Comparator<Meal>() {
                    @Override
                    public int compare(Meal o1, Meal o2) {
                        return o1.getDateTime().compareTo(o2.getDateTime());
                    }
                }).collect(Collectors.toList())
                : null;
    }
}

