package com.fourspoons.mikkureomi.profile.domain;

import com.fourspoons.mikkureomi.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="profile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", updatable = false, nullable = false)
    private Long profileId;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;
}