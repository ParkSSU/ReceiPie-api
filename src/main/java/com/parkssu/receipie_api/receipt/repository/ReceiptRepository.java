package com.parkssu.receipie_api.receipt.repository;

import com.parkssu.receipie_api.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReceiptRepository - 영수증 관련 데이터베이스 접근 인터페이스
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    /**
     * 특정 사용자의 모든 영수증 목록 조회
     * - username이 참여자로 등록된 영수증만 조회
     */
    List<Receipt> findByParticipants_Username(String username);
}
