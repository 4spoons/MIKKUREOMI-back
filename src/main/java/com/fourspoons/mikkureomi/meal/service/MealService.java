package com.fourspoons.mikkureomi.meal.service;


import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.dailyReport.service.DailyReportService;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.dto.request.MealRequestDto;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import com.fourspoons.mikkureomi.meal.repository.MealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

    private final MealRepository mealRepository;
    private final DailyReportService dailyReportService;

    @Transactional
    public Meal createMeal(Long profileId) {

        LocalDate today = LocalDate.now();

        // 1. DailyReport 조회/생성 및 Meal 연동
        DailyReport dailyReport = dailyReportService.getOrCreateDailyReport(profileId, today);

        // 2. Meal 생성 및 저장
        Meal newMeal = Meal.builder()
                .dailyReport(dailyReport)
                .build();
        Meal savedMeal = mealRepository.save(newMeal);


        // 3. DailyReport score/comment 업데이트
        dailyReportService.updateReportOnNewMeal(dailyReport);

        return savedMeal;
    }

    // 추가 기능 1: MealId로 상세 조회 (연관관계 포함)
    public MealResponseDto getMealDetailById(Long profileId, Long mealId) {
        checkAccessToMeal(profileId, mealId);
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MEAL_NOT_FOUND));

        return new MealResponseDto(meal);
    }


    // 추가 기능 2: 날짜로 Meal 리스트 조회 (연관관계 포함)
    public List<MealResponseDto> getMealsByDate(Long profileId, LocalDate date) {

        // 조회 기간 설정: 해당 날짜 00:00:00 부터 다음 날 00:00:00 이전까지
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfNextDay = date.plusDays(1).atStartOfDay();

        List<Meal> meals = mealRepository.findMealsWithDetailsByDateRange(startOfDay, endOfNextDay, profileId);

        return meals.stream()
                .map(MealResponseDto::new) // 각 Meal의 연관관계 정보도 DTO에 포함
                .collect(Collectors.toList());
    }

    public void checkAccessToMeal (Long profileId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MEAL_NOT_FOUND));

        // 작성자 ID와 현재 사용자 ID 비교
        Long mealOwnerProfileId = meal.getDailyReport().getProfile().getProfileId();

        // ID가 다르면 권한 없음 예외 발생
        if (!mealOwnerProfileId.equals(profileId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }
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