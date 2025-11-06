package com.fourspoons.mikkureomi.notification.controller;

import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.notification.dto.NotificationResponse;
import com.fourspoons.mikkureomi.notification.service.NotificationQueryService;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final ProfileService profileService;

    public NotificationController(NotificationQueryService notificationQueryService, ProfileService profileService) {
        this.notificationQueryService = notificationQueryService;
        this.profileService = profileService;
    }

    // 특정 사용자의 알림 목록을 최신순으로 조회
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications (@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());

        List<NotificationResponse> responses = notificationQueryService.getNotificationsByProfileId(profileId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());

        return ApiResponse.success(ResponseMessage.GET_NOTIFICATION_SUCCESS.getMessage(), responses);
    }

     // 특정 알림의 읽음 상태를 '읽음'(true)으로 변경
    @PatchMapping("/read/{notificationId}")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        notificationQueryService.markNotificationAsRead(notificationId);
        return ApiResponse.success(ResponseMessage.ISREAD_UPDATE_SUCCESS.getMessage(), null);
    }

     // 특정 사용자의 읽지 않은 알림 개수를 조회 (앱의 종 모양 아이콘 옆 빨간 뱃지의 후구현을 위해)
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());

        long count = notificationQueryService.getUnreadNotificationCount(profileId);
        return ApiResponse.success(ResponseMessage.REMAINING_MESSAGE_NUMBER_SUCCESS.getMessage(), count);
    }
}
