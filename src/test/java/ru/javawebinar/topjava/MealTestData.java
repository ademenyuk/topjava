package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import java.time.LocalDateTime;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_MEAL_ID_1 = START_SEQ + 3;
    public static final int USER_MEAL_ID_2 = START_SEQ + 4;
    public static final int USER_MEAL_ID_3 = START_SEQ + 5;
    public static final int ADMIN_MEAL_ID_1 = START_SEQ + 6;
    public static final int ADMIN_MEAL_ID_2 = START_SEQ + 7;

    public static final Meal user_meal_1 = new Meal(USER_MEAL_ID_1,
            LocalDateTime.parse("2022-06-20T10:00:00"), "Завтрак юзера", 600);
    public static final Meal user_meal_2 = new Meal(USER_MEAL_ID_2,
            LocalDateTime.parse("2022-06-20T15:00:00"), "Обед юзера", 1300);
    public static final Meal user_meal_3 = new Meal(USER_MEAL_ID_3,
            LocalDateTime.parse("2022-06-20T20:00:00"), "Ужин юзера", 700);
    public static final Meal admin_meal_1 = new Meal(ADMIN_MEAL_ID_1,
            LocalDateTime.parse("2022-06-20T10:00:00"), "Завтрак админа", 500);
    public static final Meal admin_meal_2 = new Meal(ADMIN_MEAL_ID_2,
            LocalDateTime.parse("2022-06-20T21:00:00"), "Ужин админа", 800);
}
