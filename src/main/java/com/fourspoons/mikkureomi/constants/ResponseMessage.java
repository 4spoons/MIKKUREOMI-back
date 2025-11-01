package com.fourspoons.mikkureomi.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    SIGNUP_SUCCESS("회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("로그인이 완료되었습니다."),
    LOGOUT_SUCCESS("로그아웃이 완료되었습니다."),
    PROFILE_UPDATED("프로필이 수정되었습니다."),
    PROFILE_FETCH_SUCCESS("프로필 조회가 완료되었습니다.");


    private final String message;
}
