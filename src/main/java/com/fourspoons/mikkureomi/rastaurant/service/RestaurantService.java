package com.fourspoons.mikkureomi.rastaurant.service;

import com.fourspoons.mikkureomi.rastaurant.RestaurantProjection;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import com.fourspoons.mikkureomi.rastaurant.dto.RestaurantResponse;
import com.fourspoons.mikkureomi.rastaurant.dto.RestaurantResponseSpecific;
import com.fourspoons.mikkureomi.rastaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    private List<RestaurantProjection> getProjections(double lat, double lng, int radius, Optional<RestaurantType> type) {
        if (type.isPresent()) {
            return restaurantRepository.findNearbyByType(lat, lng, radius, type.get().name());
        } else {
            return restaurantRepository.findNearbyAllType(lat, lng, radius);
        }
    }

    public List<RestaurantResponseSpecific> getSpecificRestaurants(double lat, double lng, int radius, Optional<RestaurantType> type) {
        List<RestaurantProjection> projections = getProjections(lat, lng, radius, type);

        return projections.stream()
                .map(proj -> new RestaurantResponseSpecific(
                        proj.getName(),
                        proj.getAddress(),
                        proj.getPhone(),
                        proj.getLatitude(),
                        proj.getLongitude(),
                        RestaurantType.valueOf(proj.getRestaurantType()),
                        proj.getDistance()
                ))
                .collect(Collectors.toList());
    }

    // Default API (RestaurantResponse DTO 사용)
    public List<RestaurantResponse> getRestaurants(double lat, double lng, int radius, Optional<RestaurantType> type) {
        List<RestaurantProjection> projections = getProjections(lat, lng, radius, type);

        return projections.stream()
                .map(proj -> new RestaurantResponse(
                        proj.getName(),
                        RestaurantType.valueOf(proj.getRestaurantType())
                ))
                .collect(Collectors.toList());
    }
}