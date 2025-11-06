package com.fourspoons.mikkureomi.food.service;

import com.fourspoons.mikkureomi.food.domain.Food;
import com.fourspoons.mikkureomi.food.dto.response.FoodSearchResponse;
import com.fourspoons.mikkureomi.food.repository.FoodRepository;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealNutrientSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    @Value("${api.food.url}")
    private String apiUrl;

    @Value("${api.food.service-key}")
    private String serviceKey;

    private final FoodRepository foodRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    // DB에서 부분 검색
    public FoodSearchResponse searchFoodByName(String keyword) {
        List<Food> foodList = foodRepository.findByNameContaining(keyword);

        return FoodSearchResponse.builder()
                .count(foodList.size())
                .query(keyword)
                .timestamp(LocalDateTime.now())
                .foods(foodList)
                .build();
    }

    public BigDecimal calNutri(BigDecimal valuePer100g, BigDecimal foodSize, BigDecimal quantity) {
        if (valuePer100g == null) return BigDecimal.ZERO;
        if (quantity == null) quantity = BigDecimal.ONE;

        // 1) gramsConsumed 결정
        BigDecimal gramsConsumed;
        if (foodSize != null && foodSize.compareTo(BigDecimal.ZERO) > 0) {
            // quantity를 '인분'으로 해석
            gramsConsumed = foodSize.multiply(quantity);
        } else {
            // foodSize 정보가 없으면 1인분=100그램으로 해석 (안전한 기본 처리)
            gramsConsumed = quantity.multiply(new BigDecimal(100));
        }

        // 2) 계산: valuePer100g * (gramsConsumed / 100)
        return valuePer100g
                .multiply(gramsConsumed)
                .divide(new BigDecimal("100"))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public MealNutrientSummary calNutriSummary(Food food, BigDecimal quantity) {
        BigDecimal size = food.getFoodSize();

        return MealNutrientSummary.builder()
                .calories(calNutri(food.getEnerc(), size, quantity))
                .carbohydrates(calNutri(food.getChocdf(), size, quantity))
                .dietaryFiber(calNutri(food.getFibtg(), size, quantity))
                .protein(calNutri(food.getProt(), size, quantity))
                .fat(calNutri(food.getFatce(), size, quantity))
                .sugars(calNutri(food.getSugar(), size, quantity))
                .sodium(calNutri(food.getNat(), size, quantity))
                .build();
    }


    // 공공데이터 API 전체 동기화
    public void syncFoodDataFromAPI() {
        int page = 1;
        int totalCount = 0;

        while (true) {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", page)
                    .queryParam("numOfRows", "100")
                    .queryParam("type", "json")
                    .build(false)
                    .toUriString();

            try {
                String response = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(response);
                JsonNode items = root.path("response").path("body").path("items");

                if (!items.isArray() || items.isEmpty()) break;

                List<Food> foodList = new ArrayList<>();

                for (JsonNode item : items) {
                    Food food = Food.builder()
                            .foodCd(item.path("foodCd").asText())
                            .foodNm(item.path("foodNm").asText())
                            .enerc(parseToBigDecimal(item.path("enerc").asText()))
                            .prot(parseToBigDecimal(item.path("prot").asText()))
                            .fatce(parseToBigDecimal(item.path("fatce").asText()))
                            .chocdf(parseToBigDecimal(item.path("chocdf").asText()))
                            .sugar(parseToBigDecimal(item.path("sugar").asText()))
                            .fibtg(parseToBigDecimal(item.path("fibtg").asText()))
                            .nat(parseToBigDecimal(item.path("nat").asText()))
                            .foodSize(parseToBigDecimal(item.path("foodSize").asText()))
                            .build();

                    foodList.add(food);
                }

                foodRepository.saveAll(foodList);
                totalCount += foodList.size();
                System.out.println("✅ " + page + "페이지 저장 완료 (" + foodList.size() + "건)");
                page++;

            } catch (Exception e) {
                throw new RuntimeException("API 동기화 실패: " + e.getMessage());
            }
        }

        System.out.println("총 " + totalCount + "건 데이터 동기화 완료!");
    }

    private BigDecimal parseToBigDecimal(String value) {
        if (value == null || value.isEmpty() || value.equals("-")) return null;
        try {
            // 단위 제거 (예: "200 g", "150.5 ml" → "200", "150.5")
            String numeric = value.replaceAll("[^0-9.]", "");
            if (numeric.isEmpty()) return null;
            return new BigDecimal(numeric);
        } catch (NumberFormatException e) {
            return null;
        }
    }


}
