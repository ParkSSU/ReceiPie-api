package com.parkssu.receipie_api.receipt.controller;

import com.parkssu.receipie_api.receipt.dto.ReceiptRequestDto;
import com.parkssu.receipie_api.receipt.entity.Receipt;
import com.parkssu.receipie_api.receipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReceiptController - 영수증 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    /**
     * 새로운 영수증 생성
     * - 영수증 정보, 항목, 구매자, 참여자를 한 번에 저장
     * - 저장 성공 시 생성된 영수증 ID 반환
     */
    @PostMapping
    public ResponseEntity<Long> createReceipt(@RequestBody ReceiptRequestDto requestDto) {
        Long receiptId = receiptService.createReceipt(requestDto);
        return ResponseEntity.ok(receiptId);
    }

    /**
     * 로그인한 사용자의 모든 영수증 조회
     * - 현재는 username을 하드코딩했지만, JWT 기반으로 추후 변경 가능
     */
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        String username = "상일";  // 임시로 이름 하드코딩
        List<Receipt> receipts = receiptService.getAllReceipts(username);
        return ResponseEntity.ok(receipts);
    }

    /**
     * 특정 영수증 삭제
     * - 영수증과 연결된 항목, 구매자, 참여자도 모두 삭제됨
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.noContent().build();
    }
}
