package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.RemoteWorkRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import com.dao.momentum.work.command.domain.repository.RemoteWorkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoteWorkFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final RemoteWorkRepository remoteWorkRepository;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // RemoteWorkRequest 로 변환하기
        RemoteWorkRequest detail = objectMapper.convertValue(
                form, RemoteWorkRequest.class
        );

        // RemoteWork 만들기
        RemoteWork remoteWork = RemoteWork.builder()
                .approveId(approveId)
                .startDate(detail.getStartDate())
                .endDate(detail.getEndDate())
                .reason(detail.getReason())
                .build();

        // 저장하기
        remoteWorkRepository.save(remoteWork);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        RemoteWork remoteWork = remoteWorkRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("재택근무 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 재택근무를 신청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 재택근무 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 재택근무 결재가 반려되었습니다.", senderName);
        };
    }

}
