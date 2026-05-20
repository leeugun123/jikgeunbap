package com.jikgeunbap;

import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class JikgeunbapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JikgeunbapServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRestaurants(RestaurantRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            // 서울 시청(37.5665, 126.9780) 인근 500m 이내 샘플 식당 30개
            repo.saveAll(List.of(

                // ── 한식 (8) ──────────────────────────────────────────────────
                Restaurant.builder().name("명동 곰탕집").category("한식")
                    .latitude(37.5660).longitude(126.9775).rating(4.3).ratingCount(156)
                    .tags("곰탕,국밥,점심특선").build(),

                Restaurant.builder().name("명동 칼국수").category("한식")
                    .latitude(37.5670).longitude(126.9785).rating(4.1).ratingCount(230)
                    .tags("칼국수,국물,든든").build(),

                Restaurant.builder().name("삼겹살파티").category("한식")
                    .latitude(37.5658).longitude(126.9790).rating(4.4).ratingCount(95)
                    .tags("삼겹살,회식,고기").build(),

                Restaurant.builder().name("비빔밥 정식집").category("한식")
                    .latitude(37.5663).longitude(126.9770).rating(4.2).ratingCount(180)
                    .tags("비빔밥,정식,건강식").build(),

                Restaurant.builder().name("설렁탕본가").category("한식")
                    .latitude(37.5672).longitude(126.9782).rating(4.0).ratingCount(310)
                    .tags("설렁탕,사골,해장").build(),

                Restaurant.builder().name("닭갈비 명가").category("한식")
                    .latitude(37.5656).longitude(126.9773).rating(4.5).ratingCount(88)
                    .tags("닭갈비,매운맛,볶음").build(),

                Restaurant.builder().name("순두부 찌개").category("한식")
                    .latitude(37.5668).longitude(126.9788).rating(3.9).ratingCount(142)
                    .tags("순두부,찌개,해장").build(),

                Restaurant.builder().name("갈비찜 전문점").category("한식")
                    .latitude(37.5661).longitude(126.9768).rating(4.3).ratingCount(72)
                    .tags("갈비찜,찜요리,특선").build(),

                // ── 중식 (4) ──────────────────────────────────────────────────
                Restaurant.builder().name("홍콩반점").category("중식")
                    .latitude(37.5661).longitude(126.9777).rating(4.2).ratingCount(175)
                    .tags("짬뽕,짜장면,탕수육").build(),

                Restaurant.builder().name("향미루").category("중식")
                    .latitude(37.5674).longitude(126.9783).rating(4.0).ratingCount(120)
                    .tags("마라탕,마라샹궈,양꼬치").build(),

                Restaurant.builder().name("차이나타운").category("중식")
                    .latitude(37.5655).longitude(126.9786).rating(3.8).ratingCount(200)
                    .tags("볶음밥,만두,중화요리").build(),

                Restaurant.builder().name("딤섬하우스").category("중식")
                    .latitude(37.5667).longitude(126.9771).rating(4.3).ratingCount(65)
                    .tags("딤섬,고급,데이트").build(),

                // ── 일식 (5) ──────────────────────────────────────────────────
                Restaurant.builder().name("스시미소").category("일식")
                    .latitude(37.5662).longitude(126.9779).rating(4.6).ratingCount(72)
                    .tags("스시,회,오마카세").build(),

                Restaurant.builder().name("라멘야").category("일식")
                    .latitude(37.5671).longitude(126.9776).rating(4.4).ratingCount(195)
                    .tags("라멘,돈코츠,국물").build(),

                Restaurant.builder().name("이자카야 긴자").category("일식")
                    .latitude(37.5658).longitude(126.9784).rating(4.1).ratingCount(110)
                    .tags("이자카야,꼬치,사케").build(),

                Restaurant.builder().name("우동본점").category("일식")
                    .latitude(37.5665).longitude(126.9791).rating(4.0).ratingCount(88)
                    .tags("우동,소바,덴뿌라").build(),

                Restaurant.builder().name("돈카츠 장인").category("일식")
                    .latitude(37.5673).longitude(126.9769).rating(4.5).ratingCount(55)
                    .tags("돈카츠,안심,가성비").build(),

                // ── 양식 (4) ──────────────────────────────────────────────────
                Restaurant.builder().name("브런치하우스").category("양식")
                    .latitude(37.5660).longitude(126.9781).rating(4.3).ratingCount(140)
                    .tags("브런치,샐러드,건강식").build(),

                Restaurant.builder().name("파스타 공방").category("양식")
                    .latitude(37.5669).longitude(126.9774).rating(4.4).ratingCount(98)
                    .tags("파스타,리조또,이탈리안").build(),

                Restaurant.builder().name("버거앤코").category("양식")
                    .latitude(37.5657).longitude(126.9787).rating(4.0).ratingCount(220)
                    .tags("버거,수제버거,감자튀김").build(),

                Restaurant.builder().name("스테이크하우스").category("양식")
                    .latitude(37.5675).longitude(126.9780).rating(4.7).ratingCount(45)
                    .tags("스테이크,와인,고급").build(),

                // ── 분식 (6) ──────────────────────────────────────────────────
                Restaurant.builder().name("엽기떡볶이").category("분식")
                    .latitude(37.5665).longitude(126.9780).rating(4.1).ratingCount(120)
                    .tags("떡볶이,분식,가성비").build(),

                Restaurant.builder().name("김밥천국").category("분식")
                    .latitude(37.5663).longitude(126.9782).rating(3.9).ratingCount(350)
                    .tags("김밥,라면,저렴").build(),

                Restaurant.builder().name("순대국밥").category("분식")
                    .latitude(37.5670).longitude(126.9778).rating(4.0).ratingCount(167)
                    .tags("순대,국밥,점심").build(),

                Restaurant.builder().name("튀김포차").category("분식")
                    .latitude(37.5659).longitude(126.9773).rating(4.2).ratingCount(88)
                    .tags("튀김,어묵,포차").build(),

                Restaurant.builder().name("떡볶이천국").category("분식")
                    .latitude(37.5664).longitude(126.9789).rating(4.1).ratingCount(145)
                    .tags("떡볶이,순대,분식").build(),

                Restaurant.builder().name("한솥도시락").category("분식")
                    .latitude(37.5666).longitude(126.9783).rating(4.0).ratingCount(200)
                    .tags("도시락,테이크아웃,한식").build(),

                // ── 카페 (3) ──────────────────────────────────────────────────
                Restaurant.builder().name("커피한잔").category("카페")
                    .latitude(37.5661).longitude(126.9776).rating(4.4).ratingCount(230)
                    .tags("커피,케이크,디저트").build(),

                Restaurant.builder().name("베이커리하우스").category("카페")
                    .latitude(37.5667).longitude(126.9783).rating(4.5).ratingCount(185)
                    .tags("빵,샌드위치,베이커리").build(),

                Restaurant.builder().name("스무디바").category("카페")
                    .latitude(37.5672).longitude(126.9780).rating(4.0).ratingCount(120)
                    .tags("스무디,건강음료,비건").build()
            ));
        };
    }
}
