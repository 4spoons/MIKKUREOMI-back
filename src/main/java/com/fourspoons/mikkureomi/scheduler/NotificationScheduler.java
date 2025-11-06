package com.fourspoons.mikkureomi.scheduler;

import com.fourspoons.mikkureomi.notification.domain.NotificationCategory;
import com.fourspoons.mikkureomi.notification.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 10,15,20 * * *")
    public void runMissedMealCheck() {
        System.out.println("[Scheduler] 식사 미등록 위험 체크 시작...");
        notificationService.checkMissedMealAndNotify();
        System.out.println("[Scheduler] 식사 미등록 위험 체크 완료.");
    }

    // 매주 금요일 저녁 6시에 실행, 다음 날이 주말인지 확인 후 알림을 발송
    @Scheduled(cron = "0 0 18 * * FRI")
    public void runHolidayAlert() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        boolean isTomorrowWeekend = tomorrow.getDayOfWeek() == DayOfWeek.SATURDAY;

        if (isTomorrowWeekend) {
            System.out.println("[Scheduler] 휴일 맞춤 알림 발송 시작...");

            List<Long> allProfileIds = notificationService.getAllProfileIdsForAlert();

            String deeplink = "mikkureomi://app/map/nearby";
            NotificationCategory category = NotificationCategory.HOLIDAY_ALERT;

            for (Long profileId : allProfileIds) {
                notificationService.createNotificationLog(
                        profileId,
                        category,
                        category.getDefaultContent(),
                        deeplink
                );
            }
            System.out.println("[Scheduler] 휴일 맞춤 알림 발송 완료.");
        }
    }
}