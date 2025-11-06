package com.fourspoons.mikkureomi.mealFood.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "meal_food")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MealFood extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_food_id", nullable = false)
    private Long mealFoodId;

    // Meal과의 다대일 관계 (외래 키 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "quantity")
    private BigDecimal quantity; // 단위: 인분

    @Column(name = "calories")
    private BigDecimal calories;

    @Column(name = "carbohydrates")
    private BigDecimal carbohydrates;

    @Column(name = "dietary_fiber")
    private BigDecimal dietaryFiber;

    @Column(name = "protein")
    private BigDecimal protein;

    @Column(name = "fat")
    private BigDecimal fat;

    @Column(name = "sugars")
    private BigDecimal sugars;

    @Column(name = "sodium")
    private BigDecimal sodium;

    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}