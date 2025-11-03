package com.fourspoons.mikkureomi.rastaurant.domain;

public enum RestaurantType {
    // 아동급식카드 가맹점
    PARTNER_STORE("아동급식카드 가맹점"),
    // 지자체 지정 착한 가격 업소
    GOOD_STORE("착한 가격 업소");

    // 추후 선한 영향력 가게 등 확장 가능
    // (현재는 선한 영향력 가게 서비스에서 제공하는 목록 파일이나 API 없음)

    private final String description;

    RestaurantType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
