package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save( meal, SecurityUtil.authUserId()));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            log.info("save meal {}, userId {}", meal, userId);
            ConcurrentHashMap<Integer, Meal> userMeals = repository.get(userId);
            if (userMeals == null) {
                userMeals = new ConcurrentHashMap<>();
            }
            userMeals.put(meal.getId(), meal);
            repository.put(userId, userMeals);
            return meal;
        }
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null || userMeals.get(meal.getId()) == null) {
            return null;
        }
        log.info("save meal {}, userId {}", meal, userId);
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, Integer userId) {
        log.info("delete meal {}, userId {}", id, userId);
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int id, Integer userId) {
        log.info("get meal {}, userId {}", id, userId);
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(Integer userId) {
        log.info("get all meals, userId {}", userId);
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ?
                userMeals.values().stream().sorted(Comparator.comparing(Meal::getDateTime)).collect(Collectors.toList())
                : null;
    }
}

