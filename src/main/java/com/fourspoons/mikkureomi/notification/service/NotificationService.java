package com.fourspoons.mikkureomi.notification.service;

import com.fourspoons.mikkureomi.meal.repository.MealRepository;
import com.fourspoons.mikkureomi.notification.domain.Notification;
import com.fourspoons.mikkureomi.notification.domain.NotificationCategory;
import com.fourspoons.mikkureomi.notification.repository.NotificationRepository;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final MealRepository mealRepository;

    // 생성자 주입
    public NotificationService(
            NotificationRepository notificationRepository,
            ProfileRepository profileRepository,
            MealRepository mealRepository) {
        this.notificationRepository = notificationRepository;
        this.profileRepository = profileRepository;
        this.mealRepository = mealRepository;
    }

    // 모든 프로필 ID 조회
    @Transactional(readOnly = true)
    public List<Long> getAllProfileIdsForAlert() {
        return profileRepository.findAllActiveProfileIds();
    }

    // 알림 로그를 생성하여 DB에 저장
    @Transactional
    public void createNotificationLog(Long profileId, NotificationCategory category, String content, String deeplinkUrl) {

        // 빌더 패턴을 사용하여 알림 로그 생성
        Notification notification = Notification.builder()
                .profileId(profileId)
                .category(category)
                .content(content)
                .deeplinkUrl(deeplinkUrl)
                .build();

        notificationRepository.save(notification);

        System.out.println("LOGGED: Profile " + profileId + "의 " + category.getTitle() + " 알림이 DB에 기록되었습니다.");
    }

    // 모든 프로필을 순회하며 48시간 내 식사 미등록 위험을 체크하고 로그 기록
    @Transactional
    public void checkMissedMealAndNotify() {
        // 1. 알림 대상인 모든 Profile ID 조회 (강제 알림)
        List<Long> allProfileIds = profileRepository.findAllActiveProfileIds();

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(48);
        String deeplink = "mikkureomi://app/uploadMeal";
        NotificationCategory category = NotificationCategory.MISSED_MEAL_ALERT;

        for (Long profileId : allProfileIds) {
            // 2. 해당 프로필의 가장 최근 식사 기록 시간 조회
            Optional<LocalDateTime> latestMealTime = mealRepository.findLatestMealTimeByProfileId(profileId);

            // 3. 결식 위험 판단
            boolean isAtRisk = latestMealTime
                    .map(time -> time.isBefore(cutoffTime)) // 기록이 있으면, 48시간 전보다 이른지 체크
                    .orElse(true); // 기록 자체가 없으면 무조건 위험 상태로 간주

            if (isAtRisk) {
                // 4. 알림 로그 내용 결정 및 기록
                String content;
                if (latestMealTime.isPresent()) {
                    long hoursSinceLastMeal = ChronoUnit.HOURS.between(latestMealTime.get(), LocalDateTime.now());
                    content = String.format("마지막 식사 후 %d시간이 지났어요. 든든하게 식사하세요!", hoursSinceLastMeal);
                } else {
                    // 최초 식사 기록이 없는 경우
                    content = category.getDefaultContent();
                }

                createNotificationLog(
                        profileId,
                        category,
                        content,
                        deeplink
                );
            }
        }
    }
}