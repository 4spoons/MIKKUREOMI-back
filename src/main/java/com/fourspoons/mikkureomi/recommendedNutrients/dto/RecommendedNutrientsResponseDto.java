package com.fourspoons.mikkureomi.recommendedNutrients.dto;

import com.fourspoons.mikkureomi.recommendedNutrients.domain.RecommendedNutrients;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendedNutrientsResponseDto {
    private Integer age;
    private String gender;

    private Integer calories;
    private Double carbohydrates;
    private Double protein;
    private Double fat;
    private Integer sodium;
    private Double sugars;
    private Double dietaryFiber;

    public static RecommendedNutrientsResponseDto from(RecommendedNutrients entity) {
        return RecommendedNutrientsResponseDto.builder()
                .age(entity.getAge())
                .gender(entity.getGender().name())
                .calories(entity.getCalories())
                .carbohydrates(entity.getCarbohydrates())
                .protein(entity.getProtein())
                .fat(entity.getFat())
                .sodium(entity.getSodium())
                .sugars(entity.getSugars())
                .dietaryFiber(entity.getDietaryFiber())
                .build();
    }
}
