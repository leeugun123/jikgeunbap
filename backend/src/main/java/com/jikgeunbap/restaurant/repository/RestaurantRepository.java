package com.jikgeunbap.restaurant.repository;

import com.jikgeunbap.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}

