package com.fourspoons.mikkureomi.mealPicture.controller;

import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealFinalSaveRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.MealPictureResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.RecognizedFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.service.MealPictureService;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/meal-pictures")
@RequiredArgsConstructor
public class MealPictureController {

    private final MealPictureService mealPictureService;
    private final ProfileService profileService;

    /** 1-1. 사진 인식 요청 (저장 없음) */
    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<RecognizedFoodResponseDto>>> recognizeFoods(@RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        List<RecognizedFoodResponseDto> recognizedFoods = mealPictureService.recognizeFoodsFromPicture(imageFile);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.RECOGNIZE_FOODS_SUCCESS.getMessage(), recognizedFoods)); // 200 OK
    }

    /** 1-2. 최종 저장 요청 (Meal, MealPicture, MealFood 모두 저장) */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<MealFoodResponseDto>>> saveFinalMeal(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("imageFile") MultipartFile imageFile, @RequestPart("request") MealFinalSaveRequestDto requestDto) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        List<MealFoodResponseDto> responseList =
                mealPictureService.saveFinalMealComposition(profileId, imageFile, requestDto);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.SAVE_FINAL_MEAL_SUCCESS.getMessage(), responseList));
    }

    /** 3. Meal ID로 MealPicture 조회 (GET by Meal ID) */
    @GetMapping("/by-meal/{mealId}")
    public ResponseEntity<ApiResponse<MealPictureResponseDto>> getMealPictureByMealId(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long mealId) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        MealPictureResponseDto responseDto = mealPictureService.getMealPictureByMealId(profileId, mealId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_PICTURE_SUCCESS.getMessage(), responseDto));
    }

//    /** 2. 특정 MealPicture 조회 (GET by ID) */
//    @GetMapping("/{pictureId}")
//    public ResponseEntity<ApiResponse<MealPictureResponseDto>> getMealPicture(@PathVariable Long pictureId) {
//        MealPictureResponseDto responseDto = mealPictureService.getMealPicture(pictureId);
//        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_PICTURE_SUCCESS.getMessage(), responseDto));
//    }

//    /** 4. MealPicture 수정 (PUT) */
//    @PutMapping("/{pictureId}")
//    public ResponseEntity<MealPictureResponseDto> updateMealPicture(@PathVariable Long pictureId, @RequestBody MealPictureRequestDto requestDto) {
//        MealPictureResponseDto responseDto = mealPictureService.updateMealPicture(pictureId, requestDto);
//        return ResponseEntity.ok(responseDto);
//    }

//    /** 5. MealPicture 삭제 (DELETE) */
//    @DeleteMapping("/{pictureId}")
//    public ResponseEntity<Void> deleteMealPicture(@PathVariable Long pictureId) {
//        mealPictureService.deleteMealPicture(pictureId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}