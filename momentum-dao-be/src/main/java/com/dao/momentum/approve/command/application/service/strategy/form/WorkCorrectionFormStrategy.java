package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.WorkCorrectionRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.WorkCorrection;
import com.dao.momentum.work.command.domain.repository.WorkCorrectionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkCorrectionFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final WorkCorrectionRepository workCorrectionRepository;

    public void saveDetail(JsonNode form, Long approveId) {
        // WorkCorrectionRequest로 변환하기
        WorkCorrectionRequest detail = objectMapper.convertValue(
                form, WorkCorrectionRequest.class
        );

        // WorkCorrection 만들기
        WorkCorrection workCorrection = WorkCorrection.builder()
                .approveId(approveId)
                .workId(detail.getWorkId())
                .beforeStartAt(detail.getBeforeStartAt())
                .beforeEndAt(detail.getBeforeEndAt())
                .afterStartAt(detail.getAfterStartAt())
                .afterEndAt(detail.getAfterEndAt())
                .reason(detail.getReason())
                .build();

        // 저장하기
        workCorrectionRepository.save(workCorrection);
    }

}
