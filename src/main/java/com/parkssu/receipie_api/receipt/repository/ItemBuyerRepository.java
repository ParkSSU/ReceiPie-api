package com.parkssu.receipie_api.receipt.repository;

import com.parkssu.receipie_api.receipt.entity.ItemBuyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ItemBuyerRepository - 특정 항목의 구매자 정보 접근 인터페이스
 */
@Repository
public interface ItemBuyerRepository extends JpaRepository<ItemBuyer, Long> {

    /**
     * 특정 항목에 속한 구매자 목록 조회
     */
    List<ItemBuyer> findByItemId(Long itemId);
}
