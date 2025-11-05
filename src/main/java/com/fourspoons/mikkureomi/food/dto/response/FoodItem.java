package com.fourspoons.mikkureomi.food.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodItem {
    private String foodCd;      // 식품코드
    private String foodNm;      // 식품명
    private String enerc;       // 에너지(kcal)
    private String prot;        // 단백질(g)
    private String fatce;       // 지방(g)
    private String chocdf;      // 탄수화물(g)
    private String sugar;       // 당류(g)
    private String nat;         // 나트륨(mg)
    private String fibtg;       // 식이섬유(g)
    private String foodSize;    // 식품중량
}
