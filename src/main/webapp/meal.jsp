<%--
  Created by IntelliJ IDEA.
  User: Anastasia
  Date: 14.06.2022
  Time: 22:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="https://example.com/functions" prefix="f" %>
<html>
<head>
    <link rel="stylesheet" href="styles.css">
    <title>Edit meal</title>
</head>
<body>
<form method="POST" action='meal' name="formEditMeal">
    <div class="field">
        <label for="date">Date and time </label>
        <input
                type="datetime-local" name="mealDate" id="date"
                value="<c:out value="${meal.dateTime}"/>"/><br/>
    </div>
    <div class="field">
        <label for="description">Description </label>
        <input
                type="text" name="description" id="description"
                value="<c:out value="${meal.description}"/>"/> <br/>
    </div>
    <div class="field">
        <label for="calories">Calories </label>
        <input
                type="number" name="calories" id="calories"
                value="<c:out value="${meal.calories}"/>"/> <br/>
    </div>
    <input
            type="number" name="mealId" hidden
            value="<c:out value="${meal.id}"/>"/>
    <button type="submit" name="button" value="save">Save</button>
    <button type="submit" name="button" value="cancel">Cancel</button>
</form>
</body>
</html>
