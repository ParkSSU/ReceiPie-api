package com.parkssu.receipie_api.receipt.service;

import com.parkssu.receipie_api.member.entity.Member;
import com.parkssu.receipie_api.member.repository.MemberRepository;
import com.parkssu.receipie_api.receipt.dto.request.ReceiptRequestDto;
import com.parkssu.receipie_api.receipt.dto.response.BuyerResponseDto;
import com.parkssu.receipie_api.receipt.dto.response.ItemResponseDto;
import com.parkssu.receipie_api.receipt.dto.response.ParticipantResponseDto;
import com.parkssu.receipie_api.receipt.dto.response.ReceiptResponseDto;
import com.parkssu.receipie_api.receipt.entity.*;
import com.parkssu.receipie_api.receipt.repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ ReceiptService - 영수증 저장, 조회, 수정, 삭제를 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;

    /**
     * 영수증 생성
     */
    @Transactional
    public Long createReceipt(ReceiptRequestDto requestDto, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 영수증 생성
        Receipt receipt = new Receipt();
        receipt.setStoreName(requestDto.getStoreName());
        receipt.setDate(requestDto.getDate());
        receipt.setTotalPrice(requestDto.getTotalPrice());
        receipt.setMember(member);

        // 참여자 생성
        List<ReceiptParticipant> participants = requestDto.getParticipants().stream()
                .map(username -> {
                    ReceiptParticipant participant = new ReceiptParticipant();
                    participant.setUsername(username);
                    participant.setReceipt(receipt);
                    return participant;
                }).collect(Collectors.toList());

        receipt.setParticipants(participants);

        // 항목 및 구매자 생성
        List<Item> items = requestDto.getItems().stream()
                .map(itemDto -> {
                    Item item = new Item();
                    item.setName(itemDto.getName());
                    item.setCount(itemDto.getCount());
                    item.setPrice(itemDto.getPrice());
                    item.setReceipt(receipt);

                    // 구매자 생성
                    List<ItemBuyer> buyers = itemDto.getBuyers().stream()
                            .map(buyerDto -> {
                                ItemBuyer buyer = new ItemBuyer();
                                buyer.setUsername(buyerDto.getUsername());
                                buyer.setCount(buyerDto.getCount());
                                buyer.setItem(item);
                                return buyer;
                            }).collect(Collectors.toList());

                    item.setBuyers(buyers);
                    return item;
                }).collect(Collectors.toList());

        receipt.setItems(items);

        Receipt savedReceipt = receiptRepository.save(receipt);
        return savedReceipt.getId();
    }

    /**
     *  특정 사용자의 영수증 목록 조회
     */
    @Transactional
    public List<ReceiptResponseDto> getReceiptsByMember(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        List<Receipt> receipts = receiptRepository.findByMember_Id(member.getId());

        return receipts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 영수증 삭제
     */
    @Transactional
    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }

    /**
     * 영수증 업데이트
     */
    @Transactional
    public Long updateReceipt(Long receiptId, ReceiptRequestDto requestDto, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("해당 영수증이 존재하지 않습니다."));

        if (!receipt.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("해당 영수증의 소유자가 아닙니다.");
        }

        // 영수증 업데이트
        receipt.setStoreName(requestDto.getStoreName());
        receipt.setDate(requestDto.getDate());
        receipt.setTotalPrice(requestDto.getTotalPrice());

        // 기존 항목 및 참여자 제거
        receipt.getItems().clear();
        receipt.getParticipants().clear();

        // 새로운 참여자 추가
        List<ReceiptParticipant> participants = requestDto.getParticipants().stream()
                .map(username -> {
                    ReceiptParticipant participant = new ReceiptParticipant();
                    participant.setUsername(username);
                    participant.setReceipt(receipt);
                    return participant;
                }).collect(Collectors.toList());

        receipt.setParticipants(participants);

        // 새로운 항목 추가
        List<Item> items = requestDto.getItems().stream()
                .map(itemDto -> {
                    Item item = new Item();
                    item.setName(itemDto.getName());
                    item.setCount(itemDto.getCount());
                    item.setPrice(itemDto.getPrice());
                    item.setReceipt(receipt);

                    // 구매자 추가
                    List<ItemBuyer> buyers = itemDto.getBuyers().stream()
                            .map(buyerDto -> {
                                ItemBuyer buyer = new ItemBuyer();
                                buyer.setUsername(buyerDto.getUsername());
                                buyer.setCount(buyerDto.getCount());
                                buyer.setItem(item);
                                return buyer;
                            }).collect(Collectors.toList());

                    item.setBuyers(buyers);
                    return item;
                }).collect(Collectors.toList());

        receipt.setItems(items);

        Receipt updatedReceipt = receiptRepository.save(receipt);
        return updatedReceipt.getId();
    }

    /**
     * ✅ Receipt → ReceiptResponseDto로 변환하는 메서드
     */
    private ReceiptResponseDto convertToResponseDto(Receipt receipt) {

        List<ItemResponseDto> itemDtos = receipt.getItems().stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getCount(),
                        item.getPrice(),
                        item.getBuyers().stream()
                                .map(buyer -> new BuyerResponseDto(buyer.getId(), buyer.getUsername(), buyer.getCount()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        List<ParticipantResponseDto> participantDtos = receipt.getParticipants().stream()
                .map(participant -> new ParticipantResponseDto(participant.getId(), participant.getUsername()))
                .collect(Collectors.toList());

        return ReceiptResponseDto.builder()
                .id(receipt.getId())
                .storeName(receipt.getStoreName())
                .date(receipt.getDate())
                .totalPrice(receipt.getTotalPrice())
                .items(itemDtos)
                .participants(participantDtos)
                .build();
    }
}
