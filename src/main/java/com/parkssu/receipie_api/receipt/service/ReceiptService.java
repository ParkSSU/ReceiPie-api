package com.parkssu.receipie_api.receipt.service;

import com.parkssu.receipie_api.member.entity.Member;
import com.parkssu.receipie_api.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public Long createReceipt(ReceiptRequestDto requestDto, String email) {
        // 1. Member 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 2. Receipt 생성
        Receipt receipt = new Receipt();
        receipt.setStoreName(requestDto.getStoreName());
        receipt.setDate(requestDto.getDate());
        receipt.setTotalPrice(requestDto.getTotalPrice());
        receipt.setMember(member); // JWT에서 추출한 사용자(Member)와 연결

        // 3. 참여자 엔티티 생성
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

        // 4. 항목 + 구매자 엔티티 생성
        List<Item> items = new ArrayList<>();
        if (requestDto.getItems() != null) {
            for (ItemDto itemDto : requestDto.getItems()) {
                Item item = new Item();
                item.setName(itemDto.getName());
                item.setCount(itemDto.getCount());
                item.setPrice(itemDto.getPrice());
                item.setReceipt(receipt);

                List<ItemBuyer> buyers = new ArrayList<>();
                if (itemDto.getBuyers() != null) {
                    for (BuyerDto buyerDto : itemDto.getBuyers()) {
                        ItemBuyer buyer = new ItemBuyer();
                        buyer.setUsername(buyerDto.getUsername());
                        buyer.setCount(buyerDto.getCount());
                        buyer.setItem(item);
                        buyers.add(buyer);
                    }
                }
                item.setBuyers(buyers);
                items.add(item);
            }
        }
        receipt.setItems(items);

        // 5. 저장
        Receipt saved = receiptRepository.save(receipt);
        return saved.getId();
    }
    /**
     * 특정 사용자의 영수증 목록 조회
     */
    @Transactional
    public List<Receipt> getReceiptsByMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        return receiptRepository.findByMember_Id(member.getId());
    }

    @Transactional
    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }

    @Transactional
    public Long updateReceipt(Long receiptId, ReceiptRequestDto requestDto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("해당 영수증이 존재하지 않습니다."));

        // 영수증 소유자 확인
        if (!receipt.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("해당 영수증의 소유자가 아닙니다.");
        }

        // 1. 기본 필드 업데이트
        receipt.setStoreName(requestDto.getStoreName());
        receipt.setDate(requestDto.getDate());
        receipt.setTotalPrice(requestDto.getTotalPrice());

        // 2. 기존 항목 및 참여자 삭제
        receipt.getItems().clear();
        receipt.getParticipants().clear();

        // 3. 새로운 참여자 추가
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

        // 4. 새로운 항목 추가
        List<Item> items = new ArrayList<>();
        if (requestDto.getItems() != null) {
            for (ItemDto itemDto : requestDto.getItems()) {
                Item item = new Item();
                item.setName(itemDto.getName());
                item.setCount(itemDto.getCount());
                item.setPrice(itemDto.getPrice());
                item.setReceipt(receipt);

                List<ItemBuyer> buyers = new ArrayList<>();
                if (itemDto.getBuyers() != null) {
                    for (BuyerDto buyerDto : itemDto.getBuyers()) {
                        ItemBuyer buyer = new ItemBuyer();
                        buyer.setUsername(buyerDto.getUsername());
                        buyer.setCount(buyerDto.getCount());
                        buyer.setItem(item);
                        buyers.add(buyer);
                    }
                }
                item.setBuyers(buyers);
                items.add(item);
            }
        }
        receipt.setItems(items);

        // 저장
        Receipt updated = receiptRepository.save(receipt);
        return updated.getId();
    }

}
