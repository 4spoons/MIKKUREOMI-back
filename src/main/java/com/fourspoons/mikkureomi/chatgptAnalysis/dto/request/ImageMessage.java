package com.fourspoons.mikkureomi.chatgptAnalysis.dto.request;

import java.util.List;

import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageMessage extends Message {
    private List<Content> content;

    public ImageMessage(String role, List<Content> content) {
        super(role);
        this.content = content;
    }
}
