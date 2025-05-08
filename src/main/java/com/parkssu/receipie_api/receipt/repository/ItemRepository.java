package com.parkssu.receipie_api.receipt.repository;

import com.parkssu.receipie_api.receipt.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ItemRepository - 영수증 내 항목 관련 데이터베이스 접근 인터페이스
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 특정 영수증에 속한 모든 항목 조회
     */
    List<Item> findByReceiptId(Long receiptId);
}
