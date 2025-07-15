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
        BusinessTrip trip = businessTripRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("출장 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format(
                    "[출장] %s님이 %s ~ %s 동안 %s로 출장을 신청했습니다. 사유: %s",
                    senderName,
                    trip.getStartDate(),
                    trip.getEndDate(),
                    trip.getPlace(),
                    trip.getReason()
            );
            case APPROVED -> String.format(
                    "[출장 승인 완료] %s님의 출장이 %s ~ %s 기간 동안 %s로 승인되었습니다.",
                    senderName,
                    trip.getStartDate(),
                    trip.getEndDate(),
                    trip.getPlace()
            );
            case REJECTED -> String.format(
                    "[출장 반려] %s님의 출장 신청이 반려되었습니다. 사유: %s",
                    senderName,
                    trip.getReason()
            );
        };
    }

}
