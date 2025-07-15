package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.BusinessTripRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.BusinessTrip;
import com.dao.momentum.work.command.domain.repository.BusinessTripRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessTripFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final BusinessTripRepository businessTripRepository;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // BusinessTripRequest로 변환하기
        BusinessTripRequest detail = objectMapper.convertValue(
                form, BusinessTripRequest.class
        );

        // ApproveProposal 만들기
        BusinessTrip businessTrip = BusinessTrip.builder()
                .approveId(approveId)
                .type(detail.getType())
                .place(detail.getPlace())
                .startDate(detail.getStartDate())
                .endDate(detail.getEndDate())
                .reason(detail.getReason())
                .cost(detail.getCost())
                .build();

        // 저장하기
        businessTripRepository.save(businessTrip);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        // 존재 여부만 확인 (예외 처리는 유지)
        businessTripRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("출장 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 출장을 신청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 출장 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 출장 결재가 반려되었습니다.", senderName);
        };
    }

}
