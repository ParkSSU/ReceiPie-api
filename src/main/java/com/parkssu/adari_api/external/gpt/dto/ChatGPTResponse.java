package com.parkssu.adari_api.external.gpt.dto;

import lombok.*;
import java.util.List;

/**
 * ChatGPTResponse는 GPT API로부터 받은 전체 응답 구조를 표현합니다.
 * - 여러 선택지(choices)가 올 수 있음
 * - 그 중 첫 번째 선택지를 주로 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponse {

    private List<Choice> choices; // GPT는 여러 응답 선택지를 줄 수 있음 (우리는 보통 첫 번째 사용)

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;      // 응답 번호 (보통 0)
        private Message message; // 실제 응답 메시지
    }
}
