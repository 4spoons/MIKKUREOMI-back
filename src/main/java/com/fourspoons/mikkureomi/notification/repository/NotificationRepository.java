package com.fourspoons.mikkureomi.notification.repository;

import com.fourspoons.mikkureomi.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 Profile의 알림을 최신순으로 조회
    List<Notification> findAllByProfileIdOrderBySentAtDesc(Long profileId);

    // 읽지 않은 알림 개수 조회
    long countByProfileIdAndIsReadFalse(Long profileId);
}