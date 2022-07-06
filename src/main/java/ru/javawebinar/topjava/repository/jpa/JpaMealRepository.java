package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = em.getReference(User.class, userId);
        meal.setUser(user);
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        }
        int updated = em.createNamedQuery(Meal.UPDATE)
                .setParameter("dateTime", meal.getDateTime())
                .setParameter("calories", meal.getCalories())
                .setParameter("description", meal.getDescription())
                .setParameter("id", meal.getId())
                .setParameter("userId", userId)
                .executeUpdate();
        if (updated == 0) throw new NotFoundException("not updated");
        return get(meal.getId(), userId);
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        try {
            return em.createNamedQuery(Meal.GET, Meal.class)
                    .setParameter("id", id)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.ALL_SORTED, Meal.class)
                .setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createNamedQuery(Meal.GET_BETWEEN, Meal.class)
                .setParameter("userId", userId)
                .setParameter("dateFrom", startDateTime)
                .setParameter("dateTo", endDateTime)
                .getResultList();
    }
}