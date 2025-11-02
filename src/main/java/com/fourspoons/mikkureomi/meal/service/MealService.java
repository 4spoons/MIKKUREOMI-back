package com.fourspoons.mikkureomi.meal.service;


import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.dto.request.MealRequestDto;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import com.fourspoons.mikkureomi.meal.repository.MealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

    private final MealRepository mealRepository;

    /** 1. 식사 등록 (Create) */
    @Transactional
    public MealResponseDto createMeal(MealRequestDto requestDto) {
        Meal meal = Meal.builder()
                .build();

        Meal savedMeal = mealRepository.save(meal);

        return new MealResponseDto(savedMeal);
    }

    /** 2. 특정 식사 조회 (Read One) */
    public MealResponseDto getMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with id: " + mealId));

        return new MealResponseDto(meal);
    }

    /** 3. 전체 식사 목록 조회 (Read All) */
    public List<MealResponseDto> getAllMeals() {
        return mealRepository.findAll().stream()
                .map(MealResponseDto::new)
                .collect(Collectors.toList());
    }

    /** 4. 식사 정보 수정 (Update) */
    @Transactional
    public MealResponseDto updateMeal(Long mealId, MealRequestDto requestDto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with id: " + mealId));

        Meal updatedMeal = Meal.builder()
                .mealId(meal.getMealId())
                .mealFoods(meal.getMealFoods())
                .mealPicture(meal.getMealPicture())
                .build();

        Meal savedMeal = mealRepository.save(updatedMeal);

        return new MealResponseDto(savedMeal);
    }

    /** 5. 식사 정보 삭제 (Delete) */
    @Transactional
    public void deleteMeal(Long mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new EntityNotFoundException("Meal not found with id: " + mealId);
        }
        // 연관관계의 cascade 설정(CascadeType.ALL, orphanRemoval = true)에 따라
        // Meal을 삭제하면 연결된 MealFood와 MealPicture도 함께 삭제됩니다.
        mealRepository.deleteById(mealId);
    }
}