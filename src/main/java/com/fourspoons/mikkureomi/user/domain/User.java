package com.fourspoons.mikkureomi.user.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}