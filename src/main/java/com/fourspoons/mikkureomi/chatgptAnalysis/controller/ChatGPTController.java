package com.fourspoons.mikkureomi.chatgptAnalysis.controller;

import java.io.IOException;

import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.ChatGPTResponseDto;
import com.fourspoons.mikkureomi.chatgptAnalysis.service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class ChatGPTController {
    private final ChatGPTService chatGPTService;

    @PostMapping("/image")
    public String imageAnalysis(@RequestParam MultipartFile image)
            throws IOException {
        ChatGPTResponseDto response = chatGPTService.requestImageAnalysis(image);
        return response.getChoices().get(0).getMessage().getContent();
    }

    @PostMapping("/text")
    public String textAnalysis(@RequestParam String requestText) {
        ChatGPTResponseDto response = chatGPTService.requestTextAnalysis(requestText);
        return response.getChoices().get(0).getMessage().getContent();
    }
}