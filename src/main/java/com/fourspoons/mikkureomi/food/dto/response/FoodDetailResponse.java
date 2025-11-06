package com.fourspoons.mikkureomi.food.dto.response;

import com.fourspoons.mikkureomi.food.domain.Food;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FoodDetailResponse {
    private Long id;
    private String foodCd;
    private String foodNm;
    private BigDecimal enerc;
    private BigDecimal prot;
    private BigDecimal fatce;
    private BigDecimal chocdf;
    private BigDecimal sugar;
    private BigDecimal fibtg;
    private BigDecimal nat;
    private BigDecimal foodSize;

    // Food 엔티티를 받아 DTO로 변환하는 정적 팩토리 메서드
    public static FoodDetailResponse from(Food food) {
        return FoodDetailResponse.builder()
                .id(food.getId())
                .foodCd(food.getFoodCd())
                .foodNm(food.getFoodNm())
                .enerc(food.getEnerc())
                .prot(food.getProt())
                .fatce(food.getFatce())
                .chocdf(food.getChocdf())
                .sugar(food.getSugar())
                .fibtg(food.getFibtg())
                .nat(food.getNat())
                .foodSize(food.getFoodSize())
                .build();
    }
}