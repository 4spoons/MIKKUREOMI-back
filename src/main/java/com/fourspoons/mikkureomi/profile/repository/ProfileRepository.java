package com.fourspoons.mikkureomi.profile.repository;

import com.fourspoons.mikkureomi.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile> findByUser_UserId(Long userId);

    // 알림 보낼 모든 프로필 id 조회
    @Query("SELECT p.profileId FROM Profile p")
    List<Long> findAllActiveProfileIds();
}
