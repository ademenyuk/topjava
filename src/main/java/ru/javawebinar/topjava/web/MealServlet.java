package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoInMemory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.TimeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MealServlet extends HttpServlet {

    private static final String INSERT_OR_EDIT_MEAL = "/meal.jsp";
    private static final String MEALS_LIST = "/meals.jsp";

    private final MealDao mealDao;

    public MealServlet() {
        mealDao = new MealDaoInMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forwardPage = "";
        String action = request.getParameter("action");

        if (action == null) {
            List<MealTo> meals = MealsUtil.filteredByStreams(mealDao.getAllMeals(), null, null, MealsUtil.CALORIESPERDAY);
            request.setAttribute("meals", meals);
            forwardPage = MEALS_LIST;
        }
        else if (action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            mealDao.deleteMeal(mealId);
            List<MealTo> meals = MealsUtil.filteredByStreams(mealDao.getAllMeals(), null, null, MealsUtil.CALORIESPERDAY);
            request.setAttribute("meals", meals);
            forwardPage = MEALS_LIST;
        } else if (action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            Meal meal = mealDao.getMealById(mealId);
            request.setAttribute("meal", meal);
            forwardPage = INSERT_OR_EDIT_MEAL;
        } else if (action.equalsIgnoreCase("insert")) {
            forwardPage = INSERT_OR_EDIT_MEAL;
        }
        request.getRequestDispatcher(forwardPage).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (request.getParameter("button").equalsIgnoreCase("save")) {
            Meal meal;
            try {
                LocalDateTime mealDate = TimeUtil.getLocalDateTime(request.getParameter("mealDate"));
                meal = new Meal(mealDate, request.getParameter("description"), Integer.parseInt(request.getParameter("calories")));

                String stringOfMealId = request.getParameter("mealId");
                if (stringOfMealId == null || stringOfMealId.equals("")) {
                    mealDao.addMeal(meal);
                } else {
                    Integer mealId = Integer.parseInt(stringOfMealId);
                    meal.setId(mealId);
                    mealDao.updateMeal(meal);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        List<MealTo> meals = MealsUtil.filteredByStreams(mealDao.getAllMeals(), null, null, MealsUtil.CALORIESPERDAY);
        request.setAttribute("meals", meals);
        request.getRequestDispatcher(MEALS_LIST).forward(request, response);
    }
}
