package com.fourspoons.mikkureomi.profile.dto;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {
    private String nickname;
    private int birthYear;
    private Gender gender;

    public static ProfileResponseDto from(Profile profile) {
        return ProfileResponseDto.builder()
                .nickname(profile.getNickname())
                .birthYear(profile.getBirthYear())
                .gender(profile.getGender())
                .build();
    }
}
