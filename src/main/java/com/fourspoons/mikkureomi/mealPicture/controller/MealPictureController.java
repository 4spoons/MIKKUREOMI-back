package com.fourspoons.mikkureomi.mealPicture.controller;

import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealFinalSaveRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealPictureRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.MealPictureResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.RecognizedFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.service.MealPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-pictures")
@RequiredArgsConstructor
public class MealPictureController {

    private final MealPictureService mealPictureService;

    /** 1-1. 사진 인식 요청 (저장 없음) */
    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<RecognizedFoodResponseDto>> recognizeFoods(@RequestPart("imageFile") MultipartFile imageFile) {
        List<RecognizedFoodResponseDto> recognizedFoods = mealPictureService.recognizeFoodsFromPicture(imageFile);
        return ResponseEntity.ok(recognizedFoods); // 200 OK
    }

    /** 1-2. 최종 저장 요청 (Meal, MealPicture, MealFood 모두 저장) */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<MealFoodResponseDto>> saveFinalMeal(@RequestPart("imageFile") MultipartFile imageFile, @RequestPart("request") MealFinalSaveRequestDto requestDto) {
        List<MealFoodResponseDto> responseList =
                mealPictureService.saveFinalMealComposition(imageFile, requestDto);
        return new ResponseEntity<>(responseList, HttpStatus.CREATED);
    }

    /** 2. 특정 MealPicture 조회 (GET by ID) */
    @GetMapping("/{pictureId}")
    public ResponseEntity<MealPictureResponseDto> getMealPicture(@PathVariable Long pictureId) {
        MealPictureResponseDto responseDto = mealPictureService.getMealPicture(pictureId);
        return ResponseEntity.ok(responseDto);
    }

    /** 3. Meal ID로 MealPicture 조회 (GET by Meal ID) */
    @GetMapping("/by-meal/{mealId}")
    public ResponseEntity<MealPictureResponseDto> getMealPictureByMealId(@PathVariable Long mealId) {
        MealPictureResponseDto responseDto = mealPictureService.getMealPictureByMealId(mealId);
        return ResponseEntity.ok(responseDto);
    }

    /** 4. MealPicture 수정 (PUT) */
    @PutMapping("/{pictureId}")
    public ResponseEntity<MealPictureResponseDto> updateMealPicture(@PathVariable Long pictureId, @RequestBody MealPictureRequestDto requestDto) {
        MealPictureResponseDto responseDto = mealPictureService.updateMealPicture(pictureId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /** 5. MealPicture 삭제 (DELETE) */
    @DeleteMapping("/{pictureId}")
    public ResponseEntity<Void> deleteMealPicture(@PathVariable Long pictureId) {
        mealPictureService.deleteMealPicture(pictureId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}