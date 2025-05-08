package com.parkssu.receipie_api.receipt.service;

import com.parkssu.receipie_api.receipt.dto.BuyerDto;
import com.parkssu.receipie_api.receipt.dto.ItemDto;
import com.parkssu.receipie_api.receipt.dto.ReceiptRequestDto;
import com.parkssu.receipie_api.receipt.entity.*;
import com.parkssu.receipie_api.receipt.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ReceiptService - 영수증 저장 트랜잭션 처리 클래스
 */
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptParticipantRepository participantRepository;

    @Transactional
    public Long createReceipt(ReceiptRequestDto requestDto) {
        // 1. Receipt 생성
        Receipt receipt = new Receipt();
        receipt.setStoreName(requestDto.getStoreName());
        receipt.setDate(requestDto.getDate());
        receipt.setTotalPrice(requestDto.getTotalPrice());

        // 2. 참여자 엔티티 생성 후 연결
        List<ReceiptParticipant> participants = new ArrayList<>();
        if (requestDto.getParticipants() != null) {
            for (String username : requestDto.getParticipants()) {
                ReceiptParticipant participant = new ReceiptParticipant();
                participant.setUsername(username);
                participant.setReceipt(receipt);
                participants.add(participant);
            }
        }
        receipt.setParticipants(participants);

        // 3. 항목 + 구매자 엔티티 생성 후 연결
        List<Item> items = new ArrayList<>();
        if (requestDto.getItems() != null) {
            for (ItemDto itemDto : requestDto.getItems()) {
                Item item = new Item();
                item.setName(itemDto.getName());
                item.setCount(itemDto.getCount());
                item.setPrice(itemDto.getPrice());
                item.setReceipt(receipt); // 영수증과 연결

                List<ItemBuyer> buyers = new ArrayList<>();
                if (itemDto.getBuyers() != null) {
                    for (BuyerDto buyerDto : itemDto.getBuyers()) {
                        ItemBuyer buyer = new ItemBuyer();
                        buyer.setUsername(buyerDto.getUsername());
                        buyer.setCount(buyerDto.getCount());
                        buyer.setItem(item); // 항목과 연결
                        buyers.add(buyer);
                    }
                }
                item.setBuyers(buyers); // 항목에 구매자 연결
                items.add(item);
            }
        }
        receipt.setItems(items); // 영수증에 항목 연결

        // 4. 저장 (연관된 모든 엔티티가 cascade로 함께 저장됨)
        Receipt saved = receiptRepository.save(receipt);

        return saved.getId();
    }
    @Transactional
    public List<Receipt> getAllReceipts(String username) {
        return receiptRepository.findByParticipants_Username(username);
    }

    @Transactional
    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }


}
