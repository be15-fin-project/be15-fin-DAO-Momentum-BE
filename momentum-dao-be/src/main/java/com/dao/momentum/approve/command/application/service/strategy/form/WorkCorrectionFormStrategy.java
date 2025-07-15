package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.WorkCorrectionRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.WorkCorrection;
import com.dao.momentum.work.command.domain.repository.WorkCorrectionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

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
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        WorkCorrection correction = workCorrectionRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("근무 수정 결재 정보가 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String beforeStart = correction.getBeforeStartAt().format(formatter);
        String beforeEnd = correction.getBeforeEndAt().format(formatter);
        String afterStart = correction.getAfterStartAt().format(formatter);
        String afterEnd = correction.getAfterEndAt().format(formatter);

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 근무 수정 결재를 요청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 근무 수정 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 근무 수정 결재가 반려되었습니다.", senderName);
        };
    }
}
