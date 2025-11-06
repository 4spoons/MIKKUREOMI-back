package com.fourspoons.mikkureomi.mealFood.service;

import com.fourspoons.mikkureomi.food.domain.Food;
import com.fourspoons.mikkureomi.food.repository.FoodRepository;
import com.fourspoons.mikkureomi.food.service.FoodService;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.service.MealService;
import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealCreateRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealNutrientSummary;
import com.fourspoons.mikkureomi.mealFood.repository.MealFoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealFoodService {

    private final MealFoodRepository mealFoodRepository;
    private final MealService mealService;
    private final FoodRepository foodRepository;
    private final FoodService foodService;


    // 1. 식사 (Meal)와 음식 목록 (MealFood) 동시 등록 (Create)
    @Transactional
    public List<MealFoodResponseDto> createMealWithFoods(Long profileId, MealCreateRequestDto requestDto) {

        MealNutrientSummary totalSummary = MealNutrientSummary.empty();

        // 1. MealFood 목록 계산 및 생성
        List<MealFood> mealFoods = new ArrayList<>();

        for (MealFoodRequestDto foodDto : requestDto.getMealFoodList()) {
            Food food = foodRepository.findById(foodDto.getFoodId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식 ID: " + foodDto.getFoodId()));

            BigDecimal quantity = foodDto.getQuantity() != null ? foodDto.getQuantity() : BigDecimal.ONE;

            // 각 음식의 영양 요약 계산
            MealNutrientSummary summary = foodService.calNutriSummary(food, quantity);

            // 총합 누적
            totalSummary = totalSummary.add(summary);

            // MealFood 엔티티 생성
            MealFood mealFood = MealFood.builder()
                    .foodName(food.getFoodNm())
                    .quantity(quantity)
                    .calories(summary.getCalories())
                    .carbohydrates(summary.getCarbohydrates())
                    .dietaryFiber(summary.getDietaryFiber())
                    .protein(summary.getProtein())
                    .fat(summary.getFat())
                    .sugars(summary.getSugars())
                    .sodium(summary.getSodium())
                    .meal(null) // 나중에 setMeal(newMeal)로 설정
                    .build();

            mealFoods.add(mealFood);
        }

        // 2. Meal 생성 및 저장
        Meal newMeal = mealService.createMeal(profileId, totalSummary);

        // Meal 연결 후 저장
        mealFoods.forEach(mf -> mf.setMeal(newMeal));
        List<MealFood> savedMealFoods = mealFoodRepository.saveAll(mealFoods);

        // 3. Response DTO 목록으로 변환하여 반환
        return savedMealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());
    }

}