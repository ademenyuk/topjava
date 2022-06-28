package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal fromDB = service.get(USER_MEAL_ID_1, USER_ID);
        assertMatch(user_meal_1, fromDB);
    }

    @Test
    public void getInvalidUserId() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID_1, ADMIN_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND_MEAL_ID, USER_ID));
    }

    @Test
    public void delete() {
        Meal created = service.create(MealTestData.getNew(), USER_ID);
        assertMatch(created, service.get(created.getId(), USER_ID));
        service.delete(created.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(created.getId(), USER_ID));
    }

    @Test
    public void deleteInvalidUserId() {
        Meal created = service.create(MealTestData.getNew(), USER_ID);
        assertMatch(created, service.get(created.getId(), USER_ID));
        assertThrows(NotFoundException.class, () -> service.delete(created.getId(), ADMIN_ID));
        assertMatch(created, service.get(created.getId(), USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND_MEAL_ID, USER_ID));
    }

    @Test
    public void getBetweenInclusiveLeft() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, user_meal_3, user_meal_2, user_meal_1);
        List<Meal> betweenInclusive = service.getBetweenInclusive(LocalDate.parse("2022-06-20"),
                LocalDate.parse("2022-06-20"), USER_ID);
        assertMatch(betweenInclusive, user_meal_2, user_meal_1);
    }

    @Test
    public void getBetweenInclusiveRight() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, user_meal_3, user_meal_2, user_meal_1);
        List<Meal> betweenInclusive = service.getBetweenInclusive(LocalDate.parse("2022-06-21"),
                LocalDate.parse("2022-06-21"), USER_ID);
        assertMatch(betweenInclusive, user_meal_3);
    }

    @Test
    public void getBetweenInclusiveEmptyResult() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, user_meal_3, user_meal_2, user_meal_1);
        List<Meal> betweenInclusive = service.getBetweenInclusive(LocalDate.parse("2019-01-01"),
                LocalDate.parse("2020-01-01"), USER_ID);
        assertThatList(betweenInclusive).isEmpty();
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, user_meal_3, user_meal_2, user_meal_1);
    }

    @Test
    public void update() {
        Meal originalFromDB = service.get(USER_MEAL_ID_1, USER_ID);
        Meal originalForUpdate = MealTestData.getUpdated();
        Meal originalBeforeUpdate = new Meal(originalForUpdate);

        assertNotMatch(originalFromDB, originalForUpdate);

        service.update(originalForUpdate, USER_ID);
        Meal updated = service.get(USER_MEAL_ID_1, USER_ID);

        assertMatch(originalBeforeUpdate, updated);

        service.update(originalFromDB, USER_ID);
    }

    @Test
    public void updateNotFound() {
        Meal meal = getNew();
        meal.setId(NOT_FOUND_MEAL_ID);
        assertThrows(NotFoundException.class, () -> service.update(meal, USER_ID));
    }

    @Test
    public void updateInvalidUser() {
        assertThrows(NotFoundException.class, () -> service.update(user_meal_1, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNew(), USER_ID);
        Meal original = MealTestData.getNew();
        original.setId(created.getId());
        assertMatch(original,created);
        assertMatch(original, service.get(created.getId(), USER_ID));
        service.delete(created.getId(), USER_ID);
    }
}