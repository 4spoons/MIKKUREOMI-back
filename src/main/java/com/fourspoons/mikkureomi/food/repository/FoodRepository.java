package com.fourspoons.mikkureomi.food.repository;

import com.fourspoons.mikkureomi.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("SELECT f FROM Food f WHERE f.foodNm LIKE %:name%")
    List<Food> findByNameContaining(String name);
}
