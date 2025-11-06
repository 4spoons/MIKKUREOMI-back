package com.fourspoons.mikkureomi.profile.dto;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailResponseDto {
    private String email;
    private String nickname;
    private int birthYear;
    private Gender gender;

    public static ProfileDetailResponseDto from(Profile profile, String email) {
        return ProfileDetailResponseDto.builder()
                .email(email)
                .nickname(profile.getNickname())
                .birthYear(profile.getBirthYear())
                .gender(profile.getGender())
                .build();
    }
}
