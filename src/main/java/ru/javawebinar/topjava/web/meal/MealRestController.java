package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

import java.util.List;

@Controller
public class MealRestController {
    private MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public Meal get(int id) {
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        return service.create(meal, authUserId());
    }

    public void delete(int id) {
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        service.update(meal, authUserId());
    }

}