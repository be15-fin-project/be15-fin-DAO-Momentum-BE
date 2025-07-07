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

    @Override
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

    @Override
    public String createNotificationContent(Long approveId, String senderName) {
        WorkCorrection correction = workCorrectionRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("근무 수정 결재 정보가 없습니다."));

        return String.format(
                "[근무 수정] %s님이 기존 출퇴근 시간(%s ~ %s)을 %s ~ %s로 수정 요청했습니다. 사유: %s",
                senderName,
                correction.getBeforeStartAt(),
                correction.getBeforeEndAt(),
                correction.getAfterStartAt(),
                correction.getAfterEndAt(),
                correction.getReason()
        );
    }
}
