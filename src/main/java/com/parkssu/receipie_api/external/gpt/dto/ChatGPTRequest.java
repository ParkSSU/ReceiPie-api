package com.parkssu.receipie_api.external.gpt.dto;

import lombok.*;
import java.util.List;

/**
 * ChatGPTRequest는 OpenAI GPT API에 요청할 때 필요한 JSON 구조를 표현합니다.
 * - 모델 이름 (예: gpt-4o)
 * - 메시지 목록 (prompt 역할)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequest {

    private String model;                  // 사용할 모델명 (gpt-4o, gpt-3.5-turbo 등)
    private List<Message> messages;         // 대화 내용 (한 개 이상 필요)

    /**
     * prompt 한 줄만 넣을 때 간편 생성용 생성자
     * 내부적으로 메시지 리스트를 만들어줌
     */
    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(new Message("user", prompt));
    }
}
