package com.fourspoons.mikkureomi.meal.repository;

import com.fourspoons.mikkureomi.meal.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    @Query("SELECT m FROM Meal m " +
            "LEFT JOIN FETCH m.mealPicture mp " +
            "LEFT JOIN FETCH m.mealFoods mf " +
            "WHERE (m.createdAt BETWEEN :startOfDay AND :endOfNextDay) AND (m.dailyReport.profile.profileId = :profileId)" +
            "ORDER BY m.createdAt ASC")
    List<Meal> findMealsWithDetailsByDateRange(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfNextDay") LocalDateTime endOfNextDay,
            @Param("profileId") Long profileId);
}