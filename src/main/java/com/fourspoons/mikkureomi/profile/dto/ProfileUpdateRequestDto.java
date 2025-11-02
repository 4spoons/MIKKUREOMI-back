package com.fourspoons.mikkureomi.profile.dto;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequestDto {
    private String nickname;
    private int birthYear;
    private Gender gender;
}