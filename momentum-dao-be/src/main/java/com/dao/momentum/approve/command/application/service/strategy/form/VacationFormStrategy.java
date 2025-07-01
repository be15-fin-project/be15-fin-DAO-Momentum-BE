package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.VacationRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.Vacation;
import com.dao.momentum.work.command.domain.repository.VacationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacationFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final VacationRepository approveProposalRepository;

    public void saveDetail(JsonNode form, Long approveId) {
        // VacationRequest 로 변환하기
        VacationRequest detail = objectMapper.convertValue(
                form, VacationRequest.class
        );

        // Vacation 만들기
        Vacation vacation = Vacation.builder()
                .approveId(approveId)
                .vacationTypeId(detail.getVacationTypeId())
                .startDate(detail.getStartDate())
                .endDate(detail.getEndDate())
                .reason(detail.getReason())
                .build();
        // 저장하기
        approveProposalRepository.save(vacation);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName) {
        Vacation vacation = approveProposalRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("휴가 결재 정보가 없습니다."));

        return String.format(
                "[휴가] %s님이 %s ~ %s 기간 동안 휴가를 신청했습니다. 사유: %s",
                senderName,
                vacation.getStartDate(),
                vacation.getEndDate(),
                vacation.getReason()
        );
    }
}
