package com.fourspoons.mikkureomi.user.dto;

import com.fourspoons.mikkureomi.profile.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    private String email;
    private String password;
    private String nickname;
    private Gender gender;
    private int age;
}