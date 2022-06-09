package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class MealServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println("<table border=\"1\" cellpadding=\"6\">");
        pw.println("<tr><th>Date</th><th>Description</th><th>Calories</th></tr>");
        MealsUtil.MEALSTO.forEach(meal -> {
            String color = meal.isExcess() ? "red" : "green";
            pw.println("<tr style=\"color:" + color + "\"><td>" + meal.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + "</td>" +
                    "<td>" + meal.getDescription() + "</td>" +
                    "<td>" + meal.getCalories() + "</td></tr>");
        });
        pw.println("</table>");

//        request.setAttribute("meals", meals);
//        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

}
