package com.fourspoons.mikkureomi.rastaurant.repository;

import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
}
