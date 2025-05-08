package com.parkssu.receipie_api.receipt.repository;

import com.parkssu.receipie_api.receipt.entity.ReceiptParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReceiptParticipantRepository - 영수증의 참여자 정보 접근 인터페이스
 */
@Repository
public interface ReceiptParticipantRepository extends JpaRepository<ReceiptParticipant, Long> {

    /**
     * 특정 영수증에 속한 참여자 목록 조회
     */
    List<ReceiptParticipant> findByReceiptId(Long receiptId);
}
