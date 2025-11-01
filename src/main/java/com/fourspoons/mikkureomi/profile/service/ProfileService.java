package com.fourspoons.mikkureomi.profile.service;

import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.user.domain.User;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
