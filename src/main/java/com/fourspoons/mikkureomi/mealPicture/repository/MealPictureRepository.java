package com.fourspoons.mikkureomi.mealPicture.repository;


import com.fourspoons.mikkureomi.mealPicture.domain.MealPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealPictureRepository extends JpaRepository<MealPicture, Long> {

    Optional<MealPicture> findByMeal_MealId(Long mealId);
}