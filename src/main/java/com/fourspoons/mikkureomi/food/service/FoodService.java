package com.fourspoons.mikkureomi.food.service;

import com.fourspoons.mikkureomi.food.dto.response.FoodResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FoodService {

    @Value("${api.food.url}")
    private String apiUrl;

    @Value("${api.food.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FoodResponse searchFoodByName(String foodName) {
        //String encodedFoodName = URLEncoder.encode(foodName, StandardCharsets.UTF_8);
        System.out.println("입력된 name = " + foodName); // 콘솔 출력

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("type", "json")
                .queryParam("foodNm", foodName)
                .build(false)
                .toUriString();

        System.out.println("url check = " + url);


        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode responseNode = root.path("response");

            return objectMapper.treeToValue(responseNode, FoodResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("API 응답 파싱 실패: " + e.getMessage());
        }
    }
}
