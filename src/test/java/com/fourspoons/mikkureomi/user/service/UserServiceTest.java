package com.fourspoons.mikkureomi.user.service;

import com.fourspoons.mikkureomi.constants.ErrorMessage;
import com.fourspoons.mikkureomi.jwt.JwtTokenProvider;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.user.domain.User;
import com.fourspoons.mikkureomi.user.dto.LoginRequestDto;
import com.fourspoons.mikkureomi.user.dto.LoginResponseDto;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import com.fourspoons.mikkureomi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ProfileService profileService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // ✅ mock 초기화 보장
    }

    // ===================== 회원가입 테스트 =====================

    @Test
    @DisplayName("회원가입 성공 시 user와 profile이 저장된다")
    void signUp_success() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("miku@example.com");
        dto.setPassword("password123");
        dto.setNickname("미꾸");
        dto.setAge(16);

        given(userRepository.existsByEmail(dto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encoded-password");

        // when
        userService.signUp(dto);

        // then
        then(userRepository).should(times(1)).save(any(User.class));
        then(profileService).should(times(1)).save(eq(dto), any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signUp_duplicateEmail() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail("miku@example.com");
        given(userRepository.existsByEmail(dto.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.EMAIL_ALREADY_EXISTS.getMessage());

        then(userRepository).should(never()).save(any());
        then(profileService).shouldHaveNoInteractions();
    }

    // ===================== 로그인 테스트 =====================

    @DisplayName("로그인 성공 - 이메일과 비밀번호가 올바르면 토큰을 반환한다")
    @Test
    void login_success() {
        // given
        LoginRequestDto dto = new LoginRequestDto("miku@example.com", "password123");

        User user = User.builder()
                .userId(1L)
                .email(dto.getEmail())
                .password("encodedPassword")
                .build();

        given(userRepository.findByEmail("miku@example.com"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword"))
                .willReturn(true);
        given(jwtTokenProvider.generateToken(anyLong(), anyString()))
                .willReturn("mockedToken");

        // when
        LoginResponseDto response = userService.login(dto);

        // then
        assertThat(response)
                .isNotNull()
                .extracting(LoginResponseDto::getToken)
                .isEqualTo("mockedToken");

        then(userRepository).should(times(1)).findByEmail("miku@example.com");
        then(passwordEncoder).should(times(1)).matches("password123", "encodedPassword");
        then(jwtTokenProvider).should(times(1)).generateToken(1L, "miku@example.com");
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 존재하지 않음")
    void login_userNotFound() {
        // given
        LoginRequestDto dto = new LoginRequestDto("miku@example.com", "password123");
        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_invalidPassword() {
        // given
        LoginRequestDto dto = new LoginRequestDto("miku@example.com", "wrongpassword");
        User user = User.builder()
                .email(dto.getEmail())
                .password("encoded-password")
                .build();

        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(dto.getPassword(), user.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.INVALID_PASSWORD.getMessage());
    }
}
