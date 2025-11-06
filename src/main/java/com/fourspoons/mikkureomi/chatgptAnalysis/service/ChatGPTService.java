package com.fourspoons.mikkureomi.chatgptAnalysis.service;

import com.fourspoons.mikkureomi.chatgptAnalysis.dto.request.ChatGPTRequestDto;
import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.ChatGPTResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChatGPTService {
    @Value("${openai.model}")
    private String apiModel;

    @Value("${openai.api-url}")
    private String apiUrl;

    private final RestTemplate template;

    public ChatGPTResponseDto requestTextAnalysis(String requestText) {
        ChatGPTRequestDto request = ChatGPTRequestDto.createTextRequest(apiModel, 500, "user", requestText);
        return template.postForObject(apiUrl, request, ChatGPTResponseDto.class);
    }

    public ChatGPTResponseDto requestImageAnalysis(MultipartFile image) throws IOException {
        String base64Image = Base64.encodeBase64String(image.getBytes());
        String imageUrl = "data:image/jpeg;base64," + base64Image;
        String requestText = "당신은 음식 이미지 분석 전문가입니다. 사용자가 제공한 이미지에서 보이는 모든 음식의 이름(식재료 또는 요리명)을 정확히 식별해야 합니다. 결과는 반드시 JSON 객체 형식의 순수 문자열로만 반환해야 하며, 마크다운 구문(예: ```json)이나 추가적인 설명, 서론/결론 텍스트를 절대 포함해서는 안 됩니다. 출력 JSON 스키마는 다음과 같습니다:\n" +
                "\n" +
                "{\n" +
                "  \"detected_foods\": [\n" +
                "    {\"name\": \"인식된 음식 이름\"},\n" +
                "    {\"name\": \"인식된 음식 이름\"}\n" +
                "  ]\n" +
                "}\n" +
                "\n" +
                "음식 이름은 한국어로 최대한 명확하고 일반적인 명칭을 사용하십시오.";
        ChatGPTRequestDto request = ChatGPTRequestDto.createImageRequest(apiModel, 500, "user", requestText, imageUrl);
        return template.postForObject(apiUrl, request, ChatGPTResponseDto.class);
    }
}