package com.fourspoons.mikkureomi.chatgptAnalysis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.Message;
import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.ResponseFormat;
import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.TextMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGPTRequestDto {
    @JsonProperty("model")
    private String model;
    @JsonProperty("messages")
    private List<Message> messages;
    @JsonProperty("max_tokens")
    private int maxTokens;
    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    public static ChatGPTRequestDto createImageRequest(String model, int maxTokens, String role, String requestText, String imageUrl) {
        TextContent textContent = new TextContent("text", requestText);
        ImageContent imageContent = new ImageContent("image_url", new ImageUrl(imageUrl));
        Message message = new ImageMessage(role, List.of(textContent, imageContent));
        ResponseFormat jsonFormat = new ResponseFormat("json_object"); // JSON 형식 강제
        return createChatGPTRequest(model, maxTokens, Collections.singletonList(message), jsonFormat);
    }

    public static ChatGPTRequestDto createTextRequest(String model, int maxTokens, String role, String requestText) {
        Message message = new TextMessage(role, requestText);
        return createChatGPTRequest(model, maxTokens, Collections.singletonList(message), null);
    }

    private static ChatGPTRequestDto createChatGPTRequest(String model, int maxTokens, List<Message> messages, ResponseFormat responseFormat) {
        return ChatGPTRequestDto.builder()
                .model(model)
                .maxTokens(maxTokens)
                .messages(messages)
                .responseFormat(responseFormat)
                .build();
    }
}