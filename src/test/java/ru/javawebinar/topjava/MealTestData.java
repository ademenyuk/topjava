package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_MEAL_ID_1 = START_SEQ + 3;
    public static final int USER_MEAL_ID_2 = START_SEQ + 4;
    public static final int USER_MEAL_ID_3 = START_SEQ + 5;
    public static final int ADMIN_MEAL_ID_1 = START_SEQ + 6;
    public static final int ADMIN_MEAL_ID_2 = START_SEQ + 7;

    public static final int NOT_FOUND_MEAL_ID = START_SEQ + 100;

    public static final Meal user_meal_1 = new Meal(USER_MEAL_ID_1,
            LocalDateTime.parse("2022-06-20T10:00:00"), "Завтрак юзера", 600);
    public static final Meal user_meal_2 = new Meal(USER_MEAL_ID_2,
            LocalDateTime.parse("2022-06-20T15:00:00"), "Обед юзера", 1300);
    public static final Meal user_meal_3 = new Meal(USER_MEAL_ID_3,
            LocalDateTime.parse("2022-06-21T20:00:00"), "Ужин юзера", 700);
    public static final Meal admin_meal_1 = new Meal(ADMIN_MEAL_ID_1,
            LocalDateTime.parse("2022-06-20T10:00:00"), "Завтрак админа", 500);
    public static final Meal admin_meal_2 = new Meal(ADMIN_MEAL_ID_2,
            LocalDateTime.parse("2022-06-20T21:00:00"), "Ужин админа", 800);

    public static Meal getNew() {
        return new Meal(LocalDateTime.parse("2020-06-28T14:41:01"), "Новая еда", 1500);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(user_meal_1);
        updated.setDescription("new description");
        updated.setCalories(1111);
        updated.setDateTime(LocalDateTime.parse("2020-06-28T14:41:01"));
        return updated;
    }
    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertNotMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isNotEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
