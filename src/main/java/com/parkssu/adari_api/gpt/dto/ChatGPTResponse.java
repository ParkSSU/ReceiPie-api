package com.parkssu.adari_api.gpt.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatGPTResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private Message message;
    }
}
