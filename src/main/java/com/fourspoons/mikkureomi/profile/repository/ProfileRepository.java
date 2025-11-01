package com.fourspoons.mikkureomi.profile.repository;

import com.fourspoons.mikkureomi.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
}
