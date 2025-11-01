package com.fourspoons.mikkureomi.profile.service;

import com.fourspoons.mikkureomi.constants.ErrorMessage;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.dto.ProfileResponseDto;
import com.fourspoons.mikkureomi.profile.dto.ProfileUpdateRequestDto;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.user.domain.User;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public void save(SignUpRequestDto dto, User user) {
        Profile profile = Profile.builder()
                .user(user)
                .age(dto.getAge())
                .gender(dto.getGender())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .nickname(dto.getNickname())
                .build();

        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.PROFILE_NOT_FOUND.getMessage()));
        return ProfileResponseDto.from(profile);
    }

    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto dto) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 프로필을 찾을 수 없습니다."));

        profile.update(dto.getNickname(), dto.getAge(), dto.getGender());
        return ProfileResponseDto.from(profile);
    }

    public void deleteProfile(Long userId) {
        profileRepository.findByUser_UserId(userId)
                .ifPresent(profileRepository::delete);
    }
}
