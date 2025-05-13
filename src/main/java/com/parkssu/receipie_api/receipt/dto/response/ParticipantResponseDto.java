package com.parkssu.receipie_api.receipt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ ParticipantResponseDto - 참여자 조회 시 응답하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDto {

    private Long id;
    private String username;
}
