package com.fourspoons.mikkureomi.food.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FoodBody {
    private List<FoodItem> items;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
}
