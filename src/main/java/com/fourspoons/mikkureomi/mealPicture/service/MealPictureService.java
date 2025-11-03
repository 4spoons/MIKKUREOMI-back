package com.fourspoons.mikkureomi.mealPicture.service;

import com.fourspoons.mikkureomi.aws.AwsS3Service;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.meal.service.MealService;
import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealFood.repository.MealFoodRepository;
import com.fourspoons.mikkureomi.mealPicture.domain.MealPicture;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealFinalSaveRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.request.MealPictureRequestDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.MealPictureResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.RecognizedFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.repository.MealPictureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealPictureService {

    private final MealPictureRepository mealPictureRepository;
    private final MealFoodRepository mealFoodRepository;
    private final AwsS3Service awsS3Service;
    private final MealService mealService;

    /** 1-1. 사진 URL을 받아 인식된 음식 목록을 반환합니다. (저장 로직 없음) */
    public List<RecognizedFoodResponseDto> recognizeFoodsFromPicture(MultipartFile imageFile) {

        // 음식 인식 과정 대체
        // 실제 구현 시 외부 AI 모델 호출 로직이 들어감
        List<MealFoodRequestDto> recognizedDtos = createDummyFoodRequests();

        return recognizedDtos.stream()
                .map(dto -> new RecognizedFoodResponseDto(
                        dto.getFoodName(), dto.getQuantity(), dto.getCalories(),
                        dto.getCarbohydrates(), dto.getDietaryFiber(),
                        dto.getProtein(), dto.getFat(), dto.getSugars()))
                .collect(Collectors.toList());
    }

    /** 1-2. 최종 확정된 음식 목록과 URL을 받아 모든 엔티티를 생성하고 저장합니다. */
    @Transactional
    public List<MealFoodResponseDto> saveFinalMealComposition(Long profileId, MultipartFile imageFile, MealFinalSaveRequestDto requestDto) {

        // 1. S3에 파일 업로드 및 URL 획득
        String imageUrl = awsS3Service.upload(imageFile);

        // 2. Meal 생성 및 저장
        Meal newMeal = mealService.createMeal(profileId);

        // 3. MealPicture 생성 및 저장
        MealPicture mealPicture = MealPicture.builder()
                .imageUrl(imageUrl)
                .meal(newMeal)
                .build();
        mealPictureRepository.save(mealPicture);

        // 4. MealFood 목록 엔티티로 변환 및 저장
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
                        .meal(newMeal)
                        .build())
                .collect(Collectors.toList());

        List<MealFood> savedMealFoods = mealFoodRepository.saveAll(mealFoods);

        // MealFood 응답 DTO 목록 반환
        return savedMealFoods.stream()
                .map(MealFoodResponseDto::new)
                .collect(Collectors.toList());
    }


    /** 3. Meal ID로 MealPicture 조회 (Read by Meal ID) */
    public MealPictureResponseDto getMealPictureByMealId(Long profileId, Long mealId) {
        mealService.checkAccessToMeal(profileId, mealId);

        MealPicture picture = mealPictureRepository.findByMeal_MealId(mealId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MEAL_PICTURE_NOT_FOUND));
        return new MealPictureResponseDto(picture);
    }

    /** 2. 특정 MealPicture 조회 (Read One) */
    public MealPictureResponseDto getMealPicture(Long pictureId) {
        MealPicture picture = mealPictureRepository.findById(pictureId)
                .orElseThrow(() -> new EntityNotFoundException("MealPicture not found with id: " + pictureId));
        return new MealPictureResponseDto(picture);
    }

    /** 4. MealPicture 수정 (Update: 주로 URL 변경) */
    @Transactional
    public MealPictureResponseDto updateMealPicture(Long pictureId, MealPictureRequestDto requestDto) {
        MealPicture mealPicture = mealPictureRepository.findById(pictureId)
                .orElseThrow(() -> new EntityNotFoundException("MealPicture not found with id: " + pictureId));

        mealPicture.update(requestDto.getUrl());

        return new MealPictureResponseDto(mealPicture);
    }

    /** 5. MealPicture 삭제 (Delete) */
    @Transactional
    public void deleteMealPicture(Long pictureId) {
        MealPicture mealPicture = mealPictureRepository.findById(pictureId)
                .orElseThrow(() -> new EntityNotFoundException("MealPicture not found with id: " + pictureId));

        mealPictureRepository.delete(mealPicture);
    }

    // 음식 인식 더미 데이터 (예시)
    private List<MealFoodRequestDto> createDummyFoodRequests() {
        // Dummy 1: 샐러드
        MealFoodRequestDto salad = new MealFoodRequestDto("샐러드", new BigDecimal("1"), new BigDecimal("180"),
                new BigDecimal("15"), new BigDecimal("5"),
                new BigDecimal("10"), new BigDecimal("10"), new BigDecimal("5"));

        // Dummy 2: 닭가슴살
        MealFoodRequestDto chicken = new MealFoodRequestDto("닭가슴살", new BigDecimal("1"), new BigDecimal("150"),
                new BigDecimal("0"), new BigDecimal("0"),
                new BigDecimal("30"), new BigDecimal("3"), new BigDecimal("0"));

        return List.of(salad, chicken);
    }
}