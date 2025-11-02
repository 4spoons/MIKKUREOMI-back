package com.fourspoons.mikkureomi.meal.controller;


import com.fourspoons.mikkureomi.meal.dto.request.MealRequestDto;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import com.fourspoons.mikkureomi.meal.service.MealService;
import com.fourspoons.mikkureomi.mealPicture.dto.response.RecognizedFoodResponseDto;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    // 추가 엔드포인트 1: Meal ID로 상세 조회
    @GetMapping("/{mealId}/detail")
    public ResponseEntity<ApiResponse<MealResponseDto>> getMealDetailById(@PathVariable Long mealId) {
        MealResponseDto responseDto = mealService.getMealDetailById(mealId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MEAL_SUCCESS.getMessage(), responseDto));
    }

    // 추가 엔드포인트 2: 날짜로 리스트 조회
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<MealResponseDto>>>getMealsByDate(@RequestParam("date") LocalDate date) {
        List<MealResponseDto> responseList = mealService.getMealsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MEAL_SUCCESS.getMessage(), responseList));
    }

/*
    // 1. 식사 등록 (POST)
    @PostMapping
    public ResponseEntity<MealResponseDto> createMeal(@RequestBody MealRequestDto requestDto) {
        MealResponseDto responseDto = mealService.createMeal(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 2. 특정 식사 조회 (GET by ID)
    @GetMapping("/{mealId}")
    public ResponseEntity<MealResponseDto> getMeal(@PathVariable Long mealId) {
        MealResponseDto responseDto = mealService.getMeal(mealId);
        return ResponseEntity.ok(responseDto);
    }

    // 3. 전체 식사 목록 조회 (GET All)
    @GetMapping
    public ResponseEntity<List<MealResponseDto>> getAllMeals() {
        List<MealResponseDto> responseList = mealService.getAllMeals();
        return ResponseEntity.ok(responseList);
    }

    // 4. 식사 정보 수정 (PUT)
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponseDto> updateMeal(@PathVariable Long mealId, @RequestBody MealRequestDto requestDto) {
        MealResponseDto responseDto = mealService.updateMeal(mealId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 5. 식사 정보 삭제 (DELETE)
    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    */
}