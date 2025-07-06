package com.dao.momentum.retention.interview.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "면담 기록 세부 조회 응답 DTO")
public record RetentionContactDetailDto(
        @Schema(description = "면담 기록 ID", example = "1001")
        Long retentionId,

        @Schema(description = "대상자 이름", example = "이하준")
        String targetName,

        @Schema(description = "대상자 사번", example = "20250020")
        String targetNo,

        @Schema(description = "대상자 부서명", example = "프론트엔드팀")
        String deptName,

        @Schema(description = "대상자 직급", example = "대리")
        String positionName,

        @Schema(description = "면담 진행자 ID", example = "101")
        Long managerId,

        @Schema(description = "면담 진행자 이름", example = "정지우")
        String managerName,

        @Schema(description = "면담 요청 사유", example = "복지 불만 비율이 높아짐")
        String reason,

        @Schema(description = "면담 요청일시", example = "2025-06-20T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "상급자 대응 보고 내용", example = "개인 면담 후 만족도 향상 확인")
        String response,

        @Schema(description = "대응 보고 일시", example = "2025-06-21T14:30:00")
        LocalDateTime responseAt,

        @Schema(description = "인사팀 피드백 내용", example = "복지 개선 요청 검토 중")
        String feedback,

        @Schema(description = "피드백 작성 가능 여부 (보고 완료 & 피드백 없음)", example = "true")
        boolean feedbackWritable,

        @Schema(description = "요청 삭제 가능 여부 (아직 대응이 없는 경우)", example = "true")
        boolean deletable
) {
}
