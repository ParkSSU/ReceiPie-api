package com.parkssu.adari_api.gpt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChatGPTRequest {
    private String model;
    private List<Message> messages;

    // prompt를 받아 메시지로 래핑
    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(new Message("user", prompt));
    }
}
