<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }

        .border-dark {
            border: 1px solid #444444;
        }

        .row {
            display: flex;
            margin-right: -15px;
            margin-left: -15px;
        }

        .filter {
            padding: 15px;
            max-width: 120px;
            text-align: center;
        }

        .button-panel {
            padding: 0.75rem 1.25rem;
        }

        .filter-button {
            cursor: pointer;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <a href="meals?action=create">Add Meal</a>
    <br><br>
    <div class="border-dark">
        <form action="meals">
            <input name = "action" value="filter" hidden>
            <div class="row">
                <div class="filter">
                    <label for="dateFrom">От даты (включая)</label><br><br>
                    <input name="dateFrom" id="dateFrom" value="${dateFrom}" type="date">
                </div>
                <div class="filter">
                    <label for="dateTo">До даты (включая)</label><br><br>
                    <input name="dateTo" id="dateTo" value="${dateTo}" type="date">
                </div>
                <div class="filter">
                    <label for="timeFrom">От времени (включая)</label><br><br>
                    <input name="timeFrom" id="timeFrom" value="${timeFrom}" type="time">
                </div>
                <div class="filter">
                    <label for="timeTo">До времени (исключая)</label><br><br>
                    <input name="timeTo" id="timeTo" value="${timeTo}" type="time">
                </div>
            </div>
            <div class="button-panel">
                <button class="filter-button" type="submit">Применить</button>
            </div>
        </form>
    </div>
    <br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${requestScope.meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>