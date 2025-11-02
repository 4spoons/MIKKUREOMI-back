package com.fourspoons.mikkureomi.meal.controller;


import com.fourspoons.mikkureomi.meal.dto.request.MealRequestDto;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import com.fourspoons.mikkureomi.meal.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
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