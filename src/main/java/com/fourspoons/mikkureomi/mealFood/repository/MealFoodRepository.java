package com.fourspoons.mikkureomi.mealFood.repository;

import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealFoodRepository extends JpaRepository<MealFood, Long> {

    // 특정 Meal에 연결된 모든 MealFood를 조회
    List<MealFood> findAllByMeal_MealId(Long mealId);
}