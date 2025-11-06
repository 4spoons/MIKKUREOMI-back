package com.fourspoons.mikkureomi.notification.dto;

import com.fourspoons.mikkureomi.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long notificationId;
    private String category;
    private String title;       // Category의 title 사용
    private String content;
    private boolean isRead;
    private LocalDateTime sentAt;
    private String deeplinkUrl;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .category(notification.getCategory().name()) // ENUM 이름을 String으로 변환
                .title(notification.getCategory().getTitle()) // ENUM에 정의된 제목 사용
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .sentAt(notification.getSentAt())
                .deeplinkUrl(notification.getDeeplinkUrl())
                .build();
    }
}
