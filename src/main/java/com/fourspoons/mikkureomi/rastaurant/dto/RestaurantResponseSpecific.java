package com.fourspoons.mikkureomi.rastaurant.dto;

import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RestaurantResponseSpecific {
    private final String name;
    private final String address;
    private final String phone;
    private final double latitude;
    private final double longitude;
    private final RestaurantType type;
    private final double distanceInMeters;

    // (Lombok을 사용하여 7개 인자를 받는 생성자를 자동 생성하도록 합니다.
    //  @Builder가 자동으로 AllArgsConstructor를 포함하므로 별도 선언은 생략 가능하나,
    //  명시적인 사용을 위해 모든 필드를 인자로 받는 생성자를 수동으로 구현하거나
    //  Lombok의 @AllArgsConstructor를 사용합니다.)

    // Service 로직 (7개 인자)에 맞추기 위해 모든 필드를 받는 생성자를 사용합니다.
    // Lombok이 @Builder와 함께 생성해주지만, 충돌 방지를 위해 명시적인 사용 방식을 추천합니다.
    public RestaurantResponseSpecific(String name, String address, String phone, double latitude, double longitude, RestaurantType type, double distanceInMeters) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.distanceInMeters = distanceInMeters;
    }
}