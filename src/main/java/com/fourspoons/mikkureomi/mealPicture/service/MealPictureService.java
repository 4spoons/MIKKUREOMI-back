package com.fourspoons.mikkureomi.mealPicture.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.aws.AwsS3Service;
import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.ChatGPTResponseDto;
import com.fourspoons.mikkureomi.chatgptAnalysis.service.ChatGPTService;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.food.domain.Food;
import com.fourspoons.mikkureomi.food.repository.FoodRepository;
import com.fourspoons.mikkureomi.food.service.FoodService;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.service.MealService;
import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealNutrientSummary;
import com.fourspoons.mikkureomi.mealFood.repository.MealFoodRepository;
import com.fourspoons.mikkureomi.mealPicture.domain.MealPicture;
import com.fourspoons.mikkureomi.mealPicture.dto.request.FoodRecognitionDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealFinalSaveRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealPictureRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.RecognizedFood;
import com.fourspoons.mikkureomi.mealPicture.dto.response.MealPictureResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.RecognizedFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.repository.MealPictureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealPictureService {

    private final MealPictureRepository mealPictureRepository;
    private final MealFoodRepository mealFoodRepository;
    private final AwsS3Service awsS3Service;
    private final MealService mealService;
    private final FoodRepository foodRepository;
    private final FoodService foodService;
    private final ChatGPTService chatGPTService;

    // 1-1. 사진 URL을 받아 인식된 음식 목록을 반환 (저장 로직 없음)
    public List<RecognizedFoodResponseDto> recognizeFoodsFromPicture(MultipartFile imageFile) throws IOException {

        // 1. ChatGPT API 호출 및 응답 파싱
        ChatGPTResponseDto response = chatGPTService.requestImageAnalysis(imageFile);
        String jsonString = response.getChoices().get(0).getMessage().getContent();

        // Food db에서 foodNm으로 인식한 음식 찾아서 id list 생성
        List<String> recognizedFoodNames = parseRecognizedFoods(jsonString);

        // 2. 스트림을 사용하여 DB 매칭 및 매핑
        List<RecognizedFoodResponseDto> responseDtos = recognizedFoodNames.stream()
                .map(foodService::findBestMatchFoodId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(foodId -> foodRepository.findById(foodId).orElseThrow(() ->
                        new IllegalStateException("매칭된 ID(" + foodId + ")의 데이터가 DB에 없습니다.")))
                .map(food -> new RecognizedFoodResponseDto(food.getId(), food.getFoodNm()))
                .collect(Collectors.toList());

        return responseDtos;
    }

    private List<String> parseRecognizedFoods(String jsonString) {
        try {
            FoodRecognitionDto dto = new ObjectMapper().readValue(jsonString, FoodRecognitionDto.class);
            return dto.getDetectedFoods().stream()
                    .map(RecognizedFood::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("GPT 응답 JSON 파싱 실패", e);
        }
    }

    // 1-2. 최종 확정된 음식 목록과 URL을 받아 모든 엔티티를 생성하고 저장
    @Transactional
    public List<MealFoodResponseDto> saveFinalMealComposition(Long profileId, MultipartFile imageFile, MealFinalSaveRequestDto requestDto) {

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

        // 3. MealPicture 생성 및 저장 (파일이 있을 경우에만 수행)
        if (imageFile != null && !imageFile.isEmpty()) {

            String imageUrl = awsS3Service.upload(imageFile);

            MealPicture mealPicture = MealPicture.builder()
                    .imageUrl(imageUrl)
                    .meal(newMeal)
                    .build();
            mealPictureRepository.save(mealPicture);
        }

        // 4. Meal 연결 후 저장
        mealFoods.forEach(mf -> mf.setMeal(newMeal));
        List<MealFood> savedMealFoods = mealFoodRepository.saveAll(mealFoods);

        // MealFood 응답 DTO 목록 반환
        return savedMealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());
    }


    // 2. Meal ID로 MealPicture 조회 (Read by Meal ID)
    public MealPictureResponseDto getMealPictureByMealId(Long profileId, Long mealId) {
        mealService.checkAccessToMeal(profileId, mealId);

        MealPicture picture = mealPictureRepository.findByMeal_MealId(mealId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MEAL_PICTURE_NOT_FOUND));
        return new MealPictureResponseDto(picture);
    }

}