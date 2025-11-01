package com.fourspoons.mikkureomi.user.service;


import com.fourspoons.mikkureomi.constants.ErrorMessage;
import com.fourspoons.mikkureomi.jwt.JwtTokenProvider;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.user.dto.LoginRequestDto;
import com.fourspoons.mikkureomi.user.dto.LoginResponseDto;
import com.fourspoons.mikkureomi.user.dto.UpdatePasswordRequestDto;
import com.fourspoons.mikkureomi.user.repository.UserRepository;
import com.fourspoons.mikkureomi.user.domain.User;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    public void signUp(SignUpRequestDto dto) {
        validateDuplicateEmail(dto.getEmail());
        User user = createUser(dto);
        userRepository.save(user);
        profileService.save(dto, user);
    }

    public User createUser(SignUpRequestDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    public void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(ErrorMessage.EMAIL_ALREADY_EXISTS.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = findUserByEmail(dto.getEmail());
        validatePassword(dto.getPassword(), user.getPassword());
        String token = generateUserToken(user);
        return new LoginResponseDto(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }


    private void validatePassword(String rawPassword, String encodedPassword) {
        System.out.println("rawPassword = " + rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PASSWORD.getMessage());
        }
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PASSWORD.getMessage());
        }

        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        profileService.deleteProfile(userId);
        userRepository.delete(user);
    }

    private String generateUserToken(User user) {
        return jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
    }
}
