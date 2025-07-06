package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Retention 지원용 원시 데이터 DTO")
public record RetentionSupportRaw(
        @Schema(description = "근속 전망 Id", example = "31")
        Long retentionId,

        @Schema(description = "사번", example = "20250001")
        String empNo,

        @Schema(description = "사원 이름", example = "김현우")
        String empName,

        @Schema(description = "부서명", example = "기획팀")
        String deptName,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        @Schema(description = "Retention 점수 (등급/유형 변환용)", example = "85")
        int retentionScore,

        @Schema(description = "평가 회차 번호", example = "2")
        int roundNo,

        @Schema(description = "요약 코멘트", example = "적극적으로 업무에 참여함")
        String summaryComment
) {
}
