package com.fourspoons.mikkureomi.recommendedNutrients.service;

import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.recommendedNutrients.domain.RecommendedNutrients;
import com.fourspoons.mikkureomi.recommendedNutrients.dto.RecommendedNutrientsResponseDto;
import com.fourspoons.mikkureomi.recommendedNutrients.repository.RecommendedNutrientsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecommendedNutrientsService {
    private final RecommendedNutrientsRepository nutrientsRepository;
    private final ProfileRepository profileRepository;

    public RecommendedNutrientsResponseDto getRecommendedNutrients(Long profileId) {

        Profile userProfile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.PROFILE_NOT_FOUND.getMessage()));

        Integer birthYear = userProfile.getBirthYear();
        Gender gender = userProfile.getGender();

        int currentYear = LocalDate.now().getYear();
        Integer currentAge = currentYear - birthYear;

        // 현재 나이를 DB의 대표 나이로 변환
        Integer dbAgeKey = getDbAgeKey(currentAge);

        RecommendedNutrients nutrients = nutrientsRepository.findByAgeAndGender(dbAgeKey, gender)
                .orElseThrow(() -> {
                    String errorMessage = String.format(
                            "%s (나이: %d, 성별: %s)",
                            ErrorMessage.RECOMMENDED_NUTRIENTS_NOT_FOUND.getMessage(),
                            currentAge,
                            gender.name()
                    );
                    return new NoSuchElementException(errorMessage);
                });

        return RecommendedNutrientsResponseDto.from(nutrients);
    }

    public Integer getDbAgeKey(int age) {
        if (age >= 6 && age <= 8) {
            return 7;
        } else if (age >= 9 && age <= 11) {
            return 10;
        } else if (age >= 12 && age <= 14) {
            return 13;
        } else if (age >= 15 && age <= 18) {
            return 16;
        } else if (age >= 19 && age <= 29) {
            return 24;
        }

        String errorMessage = String.format(
                "%s: %d",
                ErrorMessage.UNSUPPORTED_AGE_RANGE.getMessage(),
                age
        );
        throw new IllegalArgumentException(errorMessage);
    }
}