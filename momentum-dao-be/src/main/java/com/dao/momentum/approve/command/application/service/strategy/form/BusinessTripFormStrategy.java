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
    public String createNotificationContent(Long approveId, String senderName) {
        BusinessTrip trip = businessTripRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("출장 결재 정보가 없습니다."));

        return String.format(
                "[출장] %s님이 %s ~ %s 동안 %s로 출장을 신청했습니다. 사유: %s",
                senderName,
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getPlace(),
                trip.getReason()
        );
    }
}
