package com.fourspoons.mikkureomi.recommendedNutrients.domain;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommended_nutrients")
public class RecommendedNutrients implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recom_id")
    private Long recomId;

    @Column(nullable = false)
    private Integer age; // 나이 (INT)

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 칼로리 (Calories)
    @Column(nullable = false)
    private Integer calories;

    // 탄수화물 (Carbohydrates)
    @Column(nullable = false)
    private Double carbohydrates;

    // 식이섬유 (DietaryFiber)
    @Column(nullable = false)
    private Double dietaryFiber;

    // 단백질 (Protein)
    @Column(nullable = false)
    private Double protein;

    // 지방 (Fat)
    @Column(nullable = false)
    private Double fat;

    // 나트륨 (Sodium)
    @Column(nullable = false)
    private Integer sodium;

    // 당 (Sugars)
    @Column(nullable = false)
    private Double sugars;
}
