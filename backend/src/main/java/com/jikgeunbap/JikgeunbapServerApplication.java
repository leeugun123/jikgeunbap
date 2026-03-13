package com.jikgeunbap;

import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JikgeunbapServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JikgeunbapServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRestaurants(RestaurantRepository restaurantRepository) {
		return args -> {
			if (restaurantRepository.count() > 0) {
				return;
			}

			restaurantRepository.save(Restaurant.builder()
					.name("김밥천국")
					.category("한식")
					.latitude(37.5665)
					.longitude(126.9780)
					.rating(4.1)
					.ratingCount(120)
					.tags("분식,가성비")
					.build());

			restaurantRepository.save(Restaurant.builder()
					.name("파스타카페")
					.category("양식")
					.latitude(37.5667)
					.longitude(126.9775)
					.rating(4.5)
					.ratingCount(85)
					.tags("파스타,데이트")
					.build());

			restaurantRepository.save(Restaurant.builder()
					.name("한솥도시락")
					.category("도시락")
					.latitude(37.5663)
					.longitude(126.9782)
					.rating(4.0)
					.ratingCount(200)
					.tags("도시락,테이크아웃")
					.build());

			restaurantRepository.save(Restaurant.builder()
					.name("사보텐")
					.category("일식")
					.latitude(37.5669)
					.longitude(126.9778)
					.rating(4.3)
					.ratingCount(60)
					.tags("돈가스,튀김")
					.build());
		};
	}

}
