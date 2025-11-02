package com.fourspoons.mikkureomi.mealFood.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
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
    @JoinColumn(name = "meal_id", nullable = false)
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

    public void update(MealFoodRequestDto requestDto) {
        this.foodName = requestDto.getFoodName();
        this.quantity = requestDto.getQuantity();
        this.calories = requestDto.getCalories();
        this.carbohydrates = requestDto.getCarbohydrates();
        this.dietaryFiber = requestDto.getDietaryFiber();
        this.protein = requestDto.getProtein();
        this.fat = requestDto.getFat();
        this.sugars = requestDto.getSugars();
    }
}