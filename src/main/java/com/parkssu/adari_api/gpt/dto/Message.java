package com.parkssu.adari_api.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;      // "user" | "assistant" | "system"
    private String content;   // 사용자 질문 또는 응답 내용
}

