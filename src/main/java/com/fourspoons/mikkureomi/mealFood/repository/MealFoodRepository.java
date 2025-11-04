package com.fourspoons.mikkureomi.mealFood.repository;

import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealFoodRepository extends JpaRepository<MealFood, Long> {

}