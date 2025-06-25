package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.OvertimeRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.Overtime;
import com.dao.momentum.work.command.domain.repository.OvertimeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OvertimeFormStrategy implements FormDetailStrategy {
    private final ObjectMapper objectMapper;
    private final OvertimeRepository overtimeRepository;

    public void saveDetail(JsonNode form, Long approveId) {
        // OvertimeRequest로 변환하기
        OvertimeRequest detail = objectMapper.convertValue(
                form, OvertimeRequest.class
        );

        // Overtime 만들기
        Overtime overtime = Overtime.builder()
                .approveId(approveId)
                .startAt(detail.getStartAt())
                .endAt(detail.getEndAt())
                .breakTime(detail.getBreakTime())
                .reason(detail.getReason())
                .build();

        // 저장하기
        overtimeRepository.save(overtime);
    }
}
