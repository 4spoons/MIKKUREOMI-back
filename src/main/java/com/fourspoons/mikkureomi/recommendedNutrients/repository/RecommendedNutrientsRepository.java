package com.fourspoons.mikkureomi.recommendedNutrients.repository;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.recommendedNutrients.domain.RecommendedNutrients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendedNutrientsRepository extends JpaRepository<RecommendedNutrients, Long> {
    Optional<RecommendedNutrients> findByAgeAndGender(Integer age, Gender gender);
}
