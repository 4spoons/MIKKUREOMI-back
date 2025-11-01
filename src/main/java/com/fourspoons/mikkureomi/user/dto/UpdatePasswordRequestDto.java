package com.fourspoons.mikkureomi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
