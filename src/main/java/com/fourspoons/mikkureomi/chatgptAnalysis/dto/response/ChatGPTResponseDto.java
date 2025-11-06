package com.fourspoons.mikkureomi.chatgptAnalysis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponseDto {
    @JsonProperty("choices")
    private List<Choice> choices;
}