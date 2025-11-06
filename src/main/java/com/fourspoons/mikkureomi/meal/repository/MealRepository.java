package com.fourspoons.mikkureomi.meal.repository;

import com.fourspoons.mikkureomi.meal.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    // 미등록 위험 판단용 가장 최근 식사 조회
    @Query("SELECT m.createdAt FROM Meal m " +
            // Meal -> DailyReport -> Profile 순으로 조인하여 해당 Profile의 식사만 조회
            "WHERE m.dailyReport.profile.profileId = :profileId " +
            "ORDER BY m.createdAt DESC " +
            "LIMIT 1")
    Optional<LocalDateTime> findLatestMealTimeByProfileId(@Param("profileId") Long profileId);

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