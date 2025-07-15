package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.ApproveCancelRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.domain.aggregate.ApproveCancel;
import com.dao.momentum.approve.command.domain.repository.ApproveCancelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final ApproveCancelRepository approveCancelRepository;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // json 형식의 폼을 ApproveCancelRequest 타입으로 변경
        ApproveCancelRequest detail = objectMapper.convertValue(
                form, ApproveCancelRequest.class
        );

        // ApproveCancel 만들기
        ApproveCancel approveCancel = ApproveCancel.builder()
                .approveId(approveId)
                .cancelReason(detail.getCancelReason())
                .build();

        // 저장하기
        approveCancelRepository.save(approveCancel);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        ApproveCancel cancel = approveCancelRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("취소 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 결재 취소를 요청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 결재 취소가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 결재 취소가 반려되었습니다.", senderName);
        };
    }
}
