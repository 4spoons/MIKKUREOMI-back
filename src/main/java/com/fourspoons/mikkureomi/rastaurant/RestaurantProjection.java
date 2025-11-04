package com.fourspoons.mikkureomi.rastaurant;

public interface RestaurantProjection {
    Long getId();
    String getName();
    String getAddress();
    String getPhone();
    double getLatitude();
    double getLongitude();
    String getRestaurantType(); // DB에서 String으로 가져옴

    Double getDistance(); // 쿼리의 AS distance와 일치
}
