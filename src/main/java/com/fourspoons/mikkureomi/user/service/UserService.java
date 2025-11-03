package com.fourspoons.mikkureomi.user.service;


import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.jwt.JwtTokenProvider;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.user.dto.*;
import com.fourspoons.mikkureomi.user.repository.UserRepository;
import com.fourspoons.mikkureomi.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .build();
    }

    public void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorMessage.EMAIL_ALREADY_EXISTS);
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
                        new CustomException(ErrorMessage.USER_NOT_FOUND));
    }


    private void validatePassword(String rawPassword, String encodedPassword) {
        System.out.println("rawPassword = " + rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorMessage.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorMessage.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    public void deleteUser(Long userId, PasswordRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        String rawPassword = dto.getPassword();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ErrorMessage.INVALID_PASSWORD);
        }

        profileService.deleteProfile(userId);
        userRepository.delete(user);
    }

    private String generateUserToken(User user) {
        return jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
    }
}
