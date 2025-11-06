package com.fourspoons.mikkureomi.notification.service;

import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.notification.domain.Notification;
import com.fourspoons.mikkureomi.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 1. 알림 목록 조회
    public List<Notification> getNotificationsByProfileId(Long profileId) {
        // Repository에 정의한 메서드를 사용: 최신순으로 목록 조회
        return notificationRepository.findAllByProfileIdOrderBySentAtDesc(profileId);
    }

    // 2. 읽지 않은 알림 개수 조회
    public long getUnreadNotificationCount(Long profileId) {
        return notificationRepository.countByProfileIdAndIsReadFalse(profileId);
    }

    // 3. 읽음 상태로 변경
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);
    }
}