package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.ApproveProposalRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.domain.aggregate.ApproveProposal;
import com.dao.momentum.approve.command.domain.repository.ApproveProposalRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProposalFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final ApproveProposalRepository approveProposalRepository;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // ApproveProposalRequest로 변환하기
        ApproveProposalRequest detail = objectMapper.convertValue(
                form, ApproveProposalRequest.class
        );

        // ApproveProposal 만들기
        ApproveProposal approveProposal = ApproveProposal.builder()
                .approveId(approveId)
                .content(detail.getContent())
                .build();

        // 저장하기
        approveProposalRepository.save(approveProposal);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        ApproveProposal proposal = approveProposalRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("품의 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[품의 결재] %s님이 품의 결재를 요청했습니다.", senderName);
            case APPROVED -> String.format("[품의 결재 승인 완료] %s님의 품의 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[품의 결재 반려] %s님의 품의 결재가 반려되었습니다.", senderName);
        };
    }
}
