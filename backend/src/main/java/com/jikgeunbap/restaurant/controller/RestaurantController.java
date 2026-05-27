package com.jikgeunbap.restaurant.controller;

import com.jikgeunbap.restaurant.dto.RecommendationResponse;
import com.jikgeunbap.restaurant.dto.RestaurantRequest;
import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.service.RestaurantService;
import com.jikgeunbap.restaurant.service.RestaurantService.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // ── AI 추천 ───────────────────────────────────────────────────────────────

    @GetMapping("/recommend")
    public RecommendationResponse recommend(
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        return restaurantService.recommend(lat, lng);
    }

    // ── 조회 ──────────────────────────────────────────────────────────────────

    @GetMapping("/nearby")
    public List<RestaurantResponse> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "500") int radius,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "recommend") String sort
    ) {
        return restaurantService.getNearby(lat, lng, radius, category, Sort.from(sort));
    }

    @GetMapping
    public List<RestaurantResponse> getAll() {
        return restaurantService.getAll();
    }

    @GetMapping("/{id}")
    public RestaurantResponse getById(@PathVariable Long id) {
        return restaurantService.getById(id);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@RequestBody RestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(
            @PathVariable Long id,
            @RequestBody RestaurantRequest request
    ) {
        return restaurantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }
}
