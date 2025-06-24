package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "면담 기록 세부 조회 응답 DTO")
public class RetentionContactDetailDto {

    @Schema(description = "면담 기록 ID", example = "1001")
    private Long retentionId;

    @Schema(description = "대상자 이름", example = "이하준")
    private String targetName;

    @Schema(description = "대상자 사번", example = "20250020")
    private String targetNo;

    @Schema(description = "대상자 부서명", example = "프론트엔드팀")
    private String deptName;

    @Schema(description = "대상자 직급", example = "대리")
    private String positionName;

    @Schema(description = "면담 진행자 ID", example = "101")
    private Long managerId;

    @Schema(description = "면담 진행자 이름", example = "정지우")
    private String managerName;

    @Schema(description = "면담 요청 사유", example = "복지 불만 비율이 높아짐")
    private String reason;

    @Schema(description = "면담 요청일시", example = "2025-06-20T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "상급자 대응 보고 내용", example = "개인 면담 후 만족도 향상 확인")
    private String response;

    @Schema(description = "대응 보고 일시", example = "2025-06-21T14:30:00")
    private LocalDateTime responseAt;

    @Schema(description = "인사팀 피드백 내용", example = "복지 개선 요청 검토 중")
    private String feedback;

    @Schema(description = "피드백 작성 가능 여부 (보고 완료 & 피드백 없음)", example = "true")
    private boolean feedbackWritable;

    @Schema(description = "요청 삭제 가능 여부 (아직 대응이 없는 경우)", example = "true")
    private boolean deletable;
}
