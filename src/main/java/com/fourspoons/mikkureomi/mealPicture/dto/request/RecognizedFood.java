package com.fourspoons.mikkureomi.mealPicture.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GPT 응답 JSON의 리스트 내부 객체: {"name": "..."}
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecognizedFood {
    private String name;
}
