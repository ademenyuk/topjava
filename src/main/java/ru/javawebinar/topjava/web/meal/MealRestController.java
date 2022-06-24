package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MealRestController {

    private final MealService service;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("get all meals");
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(LocalDate dateFrom, LocalDate dateTo, LocalTime timeFrom, LocalTime timeTo) {
        log.info("get all meals from {} to {}, {} to {}", dateFrom, dateTo, timeFrom, timeTo);
        return MealsUtil.getFilteredTos(service.getAll(dateFrom, dateTo, authUserId()), authUserCaloriesPerDay(),
                timeFrom, timeTo);
    }

    public Meal get(int id) {
        log.info("get meal {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create meal {}", meal);
        return service.create(meal, authUserId());
    }

    public void delete(int id) {
        log.info("delete meal {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update meal {}, id = {}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }

}