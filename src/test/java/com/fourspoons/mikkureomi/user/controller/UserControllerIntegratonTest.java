package com.fourspoons.mikkureomi.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.user.dto.LoginRequestDto;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 시 200 OK와 성공 메시지를 반환한다")
    void signUp_success() throws Exception {
        // given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("test1234");
        dto.setNickname("테스트");
        dto.setAge(25);
        dto.setGender(Gender.FEMALE);

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다.")) // ResponseMessage.SIGNUP_SUCCESS.getMessage()
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰을 반환한다")
    void login_success() throws Exception {
        // given: 이미 회원가입된 유저를 가정
        SignUpRequestDto signUpDto = new SignUpRequestDto();
        signUpDto.setEmail("login@example.com");
        signUpDto.setPassword("login1234");
        signUpDto.setNickname("로그인테스트");
        signUpDto.setAge(20);
        signUpDto.setGender(Gender.MALE);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk());

        LoginRequestDto loginDto = new LoginRequestDto("login@example.com", "login1234");

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_invalidPassword() throws Exception {
        // given: 회원가입 먼저
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("wrongpw@example.com");
        dto.setPassword("correctpw");
        dto.setNickname("잘못된비번");
        dto.setAge(22);
        dto.setGender(Gender.MALE);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        LoginRequestDto loginDto = new LoginRequestDto("wrongpw@example.com", "wrongpw");

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest()) // GlobalExceptionHandler 적용 시
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 올바르지 않습니다."))
                .andDo(print());
    }
}
