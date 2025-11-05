package com.fourspoons.mikkureomi.food.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodHeader {
    private String resultCode;
    private String resultMsg;
}
