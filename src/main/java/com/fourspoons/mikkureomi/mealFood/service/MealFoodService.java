package com.fourspoons.mikkureomi.mealFood.service;

import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.repository.MealRepository;
import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealCreateRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodListResponse;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealFood.repository.MealFoodRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealFoodService {

    private final MealRepository mealRepository;
    private final MealFoodRepository mealFoodRepository;


    /** 1. 식사 (Meal)와 음식 목록 (MealFood) 동시 등록 (Create) */
    @Transactional
    public List<MealFoodResponseDto> createMealWithFoods(MealCreateRequestDto requestDto) {

        // 1. Meal 엔티티 생성 및 저장 (기본 시간 정보는 BaseTimeEntity가 처리)
        Meal newMeal = Meal.builder().build(); // 빈 Meal 생성
        Meal savedMeal = mealRepository.save(newMeal);

        // 2. MealFood 목록을 엔티티로 변환 및 저장
        List<MealFood> mealFoods = requestDto.getMealFoodList().stream()
                .map(foodDto -> MealFood.builder()
                        .foodName(foodDto.getFoodName())
                        .quantity(foodDto.getQuantity())
                        .calories(foodDto.getCalories())
                        .carbohydrates(foodDto.getCarbohydrates())
                        .dietaryFiber(foodDto.getDietaryFiber())
                        .protein(foodDto.getProtein())
                        .fat(foodDto.getFat())
                        .sugars(foodDto.getSugars())
                        .meal(savedMeal) // 생성된 Meal과 연결
                        .build())
                .collect(Collectors.toList());

        // 목록 일괄 저장
        List<MealFood> savedMealFoods = mealFoodRepository.saveAll(mealFoods);

        // 3. Response DTO 목록으로 변환하여 반환
        return savedMealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());
    }

    /** 2. 특정 식사에 속한 음식 목록 조회 (Read List by Meal ID) */
    public MealFoodListResponse getMealFoodsByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MEAL_NOT_FOUND));

        List<MealFood> mealFoods = mealFoodRepository.findAllByMeal_MealId(meal.getMealId());

        List<MealFoodResponseDto> mealFoodDtos = mealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());

        // Wrapper DTO에 목록과 개수를 담아 반환
        return new MealFoodListResponse(mealFoodDtos.size(), mealFoodDtos);
    }

    /** 3. 특정 MealFood 단일 수정 (Update) */
    @Transactional
    public MealFoodResponseDto updateMealFood(Long mealFoodId, MealFoodRequestDto requestDto) {
        MealFood mealFood = mealFoodRepository.findById(mealFoodId)
                .orElseThrow(() -> new EntityNotFoundException("MealFood not found with id: " + mealFoodId));

        mealFood.update(requestDto);

        return new MealFoodResponseDto(mealFood);
    }

    /** 4. 특정 MealFood 삭제 (Delete) */
    @Transactional
    public void deleteMealFood(Long mealFoodId) {
        if (!mealFoodRepository.existsById(mealFoodId)) {
            throw new EntityNotFoundException("MealFood not found with id: " + mealFoodId);
        }
        mealFoodRepository.deleteById(mealFoodId);
    }
}