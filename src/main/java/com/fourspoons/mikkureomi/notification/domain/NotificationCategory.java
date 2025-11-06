package com.fourspoons.mikkureomi.notification.domain;

public enum NotificationCategory {

    // 1. 식사 미등록/결식 위험 판단 알림
    MISSED_MEAL_ALERT("식사 미등록 알림", "최근 식사 기록이 없어요. 든든하게 식사하셨나요?"),

    // 2. 휴일 맞춤 알림
    HOLIDAY_ALERT("휴일 맞춤 알림", "주말에도 걱정 마세요! 근처 이용 가능한 가맹점을 확인해 보세요."),

    //  3. 일일 영양 점수 70점 달성 알림
    GOOD_SCORE_ALERT("오늘의 목표 달성", "일일 식사 영양 점수 70점 이상 달성! 균형 잡힌 식사를 이어가고 있어요."),

    // (기타 알림은 제거하거나 필요하다면 NOTICE 등으로 통합)
    NOTICE("미꾸러미 공지", "서비스 관련 중요한 공지사항이 있어요.");

    private final String title;
    private final String defaultContent;

    // 생성자
    NotificationCategory(String title, String defaultContent) {
        this.title = title;
        this.defaultContent = defaultContent;
    }

    // Getter
    public String getTitle() {
        return title;
    }

    public String getDefaultContent() {
        return defaultContent;
    }
}