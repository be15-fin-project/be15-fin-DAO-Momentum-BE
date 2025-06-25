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
                .storeAddress(detail.getAddress())
                .usedAt(detail.getUsedAt())
                .build();

        // 저장하기
        approveReceiptRepository.save(approveReceipt);
    }

}
