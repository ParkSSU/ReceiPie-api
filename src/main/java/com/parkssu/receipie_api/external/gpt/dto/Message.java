package com.parkssu.receipie_api.external.gpt.dto;

import lombok.*;

/**
 * Message는 GPT API에서 사용하는 단일 대화 메시지를 표현합니다.
 * - role: 발화자 역할 ("user", "assistant", "system" 중 하나)
 * - content: 메시지 내용 (텍스트)
 *   Message 객체는 단순히 "GPT 규격을 맞추기 위한 포장지" 정도
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String role;       // 예: "user", "assistant"
    private String content;    // 예: "이 문장을 요약해줘"
}
