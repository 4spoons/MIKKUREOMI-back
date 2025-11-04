package com.fourspoons.mikkureomi.rastaurant.repository;

import com.fourspoons.mikkureomi.rastaurant.RestaurantProjection;
import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 단순 타입 필터링 (위치 기반이 아닌 경우)
    List<Restaurant> findByRestaurantType(RestaurantType restaurantType);

    // [위치 기반 + 타입 필터링] 쿼리
    // 반환 타입: List<RestaurantProjection>
    @Query(value = """
        SELECT 
            r.*, ( 
                6371000 * acos(
                    cos(radians(:lat)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:lng)) 
                    + sin(radians(:lat)) * sin(radians(r.latitude))
                )
            ) AS distance
        FROM restaurant r
        WHERE r.restaurant_type = :type 
          AND (
                6371000 * acos(
                    cos(radians(:lat)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:lng)) 
                    + sin(radians(:lat)) * sin(radians(r.latitude))
                )
              ) <= :radius 
        ORDER BY distance
        """, nativeQuery = true)
    List<RestaurantProjection> findNearbyByType(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") int radius,
            @Param("type") String type
    );

    // [위치 기반 + 전체 타입] 쿼리
    // 반환 타입: List<RestaurantProjection>
    @Query(value = """
        SELECT 
            r.*, ( 
                6371000 * acos(
                    cos(radians(:lat)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:lng)) 
                    + sin(radians(:lat)) * sin(radians(r.latitude))
                )
            ) AS distance
        FROM restaurant r
        WHERE 
        (
            6371000 * acos(
                cos(radians(:lat)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:lng)) 
                + sin(radians(:lat)) * sin(radians(r.latitude))
            )
        ) <= :radius 
        ORDER BY distance
        """, nativeQuery = true)
    List<RestaurantProjection> findNearbyAllType(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") int radius
    );
}