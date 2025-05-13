package com.parkssu.receipie_api.receipt.controller;

import com.parkssu.receipie_api.receipt.dto.request.ReceiptRequestDto;
import com.parkssu.receipie_api.receipt.dto.response.ReceiptResponseDto;
import com.parkssu.receipie_api.receipt.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ ReceiptController - 영수증 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipt", description = "영수증 관련 API")
public class ReceiptController {

    private final ReceiptService receiptService;

    /**
     * ✅ 영수증 생성
     */
    @Operation(summary = "영수증 생성", description = "로그인된 사용자가 새로운 영수증을 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createReceipt(
            @RequestBody ReceiptRequestDto requestDto,
            Authentication authentication) {

        String email = (String) authentication.getPrincipal();
        Long receiptId = receiptService.createReceipt(requestDto, email);
        return ResponseEntity.ok(receiptId);
    }

    /**
     * ✅ 로그인 사용자의 영수증 목록 조회
     */
    @Operation(summary = "로그인 사용자의 영수증 목록 조회", description = "JWT를 통해 인증된 사용자의 모든 영수증을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReceiptResponseDto>> getReceiptsByMember(Authentication authentication) {

        String email = (String) authentication.getPrincipal();
        List<ReceiptResponseDto> receiptDtos = receiptService.getReceiptsByMember(email);
        return ResponseEntity.ok(receiptDtos);
    }

    /**
     * ✅ 영수증 삭제
     */
    @Operation(summary = "영수증 삭제", description = "영수증 ID를 기반으로 해당 영수증 및 관련 데이터를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 영수증 수정
     */
    @Operation(summary = "영수증 수정", description = "영수증 ID를 기반으로 해당 영수증의 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateReceipt(
            @PathVariable Long id,
            @RequestBody ReceiptRequestDto requestDto,
            Authentication authentication) {

        String email = (String) authentication.getPrincipal();
        Long updatedId = receiptService.updateReceipt(id, requestDto, email);
        return ResponseEntity.ok(updatedId);
    }
}
