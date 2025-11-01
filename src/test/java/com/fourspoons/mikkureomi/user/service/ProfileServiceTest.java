package com.fourspoons.mikkureomi.user.service;


import com.fourspoons.mikkureomi.constants.ErrorMessage;
import com.fourspoons.mikkureomi.profile.domain.Gender;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.dto.ProfileResponseDto;
import com.fourspoons.mikkureomi.profile.dto.ProfileUpdateRequestDto;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.user.domain.User;
import com.fourspoons.mikkureomi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encoded-password")
                .build();

        testProfile = Profile.builder()
                .profileId(1L)
                .user(testUser)
                .nickname("미꾸")
                .age(25)
                .gender(Gender.FEMALE)
                .build();
    }

    // ===================== 프로필 조회 테스트 =====================

    @Test
    @DisplayName("프로필 조회 성공 시 ProfileResponseDto를 반환한다")
    void getProfile_success() {
        // given
        given(profileRepository.findByUser_UserId(1L)).willReturn(Optional.of(testProfile));

        // when
        ProfileResponseDto result = profileService.getProfile(1L);

        // then
        assertThat(result.getNickname()).isEqualTo("미꾸");
        assertThat(result.getAge()).isEqualTo(25);
        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
        then(profileRepository).should(times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("프로필 조회 실패 - 프로필이 존재하지 않으면 예외 발생")
    void getProfile_notFound() {
        // given
        given(profileRepository.findByUser_UserId(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> profileService.getProfile(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PROFILE_NOT_FOUND.getMessage());

        then(profileRepository).should(times(1)).findByUser_UserId(1L);
    }

    // ===================== 프로필 수정 테스트 =====================
    // TODO: 프로필 수정 테스트 구현
}