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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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


    /** 1. 식사 (Meal)와 음식 목록 (MealFood) 동시 등록 (Create) */
    @Transactional
    public List<MealFoodResponseDto> createMealWithFoods(Long profileId, MealCreateRequestDto requestDto) {

        // 0. MealFood의 영양 성분 합계 계산
        MealNutrientSummary nutrientSummary = getSummaryOfMealFoods(requestDto.getMealFoodList());

        // 1. Meal 생성 및 저장
        Meal newMeal = mealService.createMeal(profileId, nutrientSummary);

        // 2. MealFood 목록을 엔티티로 변환 및 저장
        List<MealFood> mealFoods = requestDto.getMealFoodList().stream()
                .map(foodDto -> {
                    Food food = foodRepository.findById(foodDto.getFoodId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식 ID: " + foodDto.getFoodId()));

                    BigDecimal quantity = foodDto.getQuantity() != null ? foodDto.getQuantity() : BigDecimal.ONE;
                    BigDecimal foodSize = food.getFoodSize(); // DB에 저장된 1회 제공량 (g) - BigDecimal

                    BigDecimal calories = foodService.calNutri(food.getEnerc(), foodSize, quantity);
                    BigDecimal carbohydrates = foodService.calNutri(food.getChocdf(), foodSize, quantity);
                    BigDecimal dietaryFiber = foodService.calNutri(food.getFibtg(), foodSize, quantity);
                    BigDecimal protein = foodService.calNutri(food.getProt(), foodSize, quantity);
                    BigDecimal fat = foodService.calNutri(food.getFatce(), foodSize, quantity);
                    BigDecimal sugars = foodService.calNutri(food.getSugar(), foodSize, quantity);
                    BigDecimal sodium = foodService.calNutri(food.getNat(), foodSize, quantity);

                    return MealFood.builder()
                            .foodName(food.getFoodNm())
                            .quantity(quantity)
                            .calories(calories)
                            .carbohydrates(carbohydrates)
                            .dietaryFiber(dietaryFiber)
                            .protein(protein)
                            .fat(fat)
                            .sugars(sugars)
                            .sodium(sodium)
                            .meal(newMeal)
                            .build();
                })
                .collect(Collectors.toList());

        // 목록 일괄 저장
        List<MealFood> savedMealFoods = mealFoodRepository.saveAll(mealFoods);

        // 3. Response DTO 목록으로 변환하여 반환
        return savedMealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());
    }

    public MealNutrientSummary getSummaryOfMealFoods(List<MealFoodRequestDto> foodDtos) {

        // 초기값 설정
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalCarbohydrates = BigDecimal.ZERO;
        BigDecimal totalDietaryFiber = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSugars = BigDecimal.ZERO;
        BigDecimal totalSodium = BigDecimal.ZERO;

        for (MealFoodRequestDto dto : foodDtos) {
            Food food = foodRepository.findById(dto.getFoodId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식 ID: " + dto.getFoodId()));

            BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : BigDecimal.ONE;
            BigDecimal foodSize = food.getFoodSize();

            totalCalories = totalCalories.add(foodService.calNutri(food.getEnerc(), foodSize, quantity));
            totalCarbohydrates = totalCarbohydrates.add(foodService.calNutri(food.getChocdf(), foodSize, quantity));
            totalDietaryFiber = totalDietaryFiber.add(foodService.calNutri(food.getFibtg(), foodSize, quantity));
            totalProtein = totalProtein.add(foodService.calNutri(food.getProt(), foodSize, quantity));
            totalFat = totalFat.add(foodService.calNutri(food.getFatce(), foodSize, quantity));
            totalSugars = totalSugars.add(foodService.calNutri(food.getSugar(), foodSize, quantity));
            totalSodium = totalSodium.add(foodService.calNutri(food.getNat(), foodSize, quantity));
        }


        return MealNutrientSummary.builder()
                .totalCalories(totalCalories)
                .totalCarbohydrates(totalCarbohydrates)
                .totalDietaryFiber(totalDietaryFiber)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .totalSugars(totalSugars)
                .totalSodium(totalSodium)
                .build();
    }

    /** 3. 특정 MealFood 단일 수정 (Update) */
//    @Transactional
//    public MealFoodResponseDto updateMealFood(Long mealFoodId, MealFoodRequestDto requestDto) {
//        MealFood mealFood = mealFoodRepository.findById(mealFoodId)
//                .orElseThrow(() -> new EntityNotFoundException("MealFood not found with id: " + mealFoodId));
//
//        mealFood.update(requestDto);
//
//        return new MealFoodResponseDto(mealFood);
//    }

    /** 4. 특정 MealFood 삭제 (Delete) */
    @Transactional
    public void deleteMealFood(Long mealFoodId) {
        if (!mealFoodRepository.existsById(mealFoodId)) {
            throw new EntityNotFoundException("MealFood not found with id: " + mealFoodId);
        }
        mealFoodRepository.deleteById(mealFoodId);
    }
}