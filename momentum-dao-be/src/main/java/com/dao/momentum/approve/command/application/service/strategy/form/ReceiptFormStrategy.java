package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.ApproveReceiptRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.domain.aggregate.ApproveReceipt;
import com.dao.momentum.approve.command.domain.repository.ApproveReceiptRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final ApproveReceiptRepository approveReceiptRepository;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // ApproveReceiptRequest로 변환하기
        ApproveReceiptRequest detail = objectMapper.convertValue(
                form, ApproveReceiptRequest.class
        );

        // ApproveReceipt 만들기
        ApproveReceipt approveReceipt = ApproveReceipt.builder()
                .approveId(approveId)
                .receiptType(detail.getReceiptType())
                .storeName(detail.getStoreName())
                .amount(detail.getAmount())
                .address(detail.getAddress())
                .usedAt(detail.getUsedAt())
                .build();

        // 저장하기
        approveReceiptRepository.save(approveReceipt);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        ApproveReceipt receipt = approveReceiptRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("영수증 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 영수증 결재를 신청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 영수증 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 영수증 결재가 반려되었습니다.", senderName);
        };
    }
}
