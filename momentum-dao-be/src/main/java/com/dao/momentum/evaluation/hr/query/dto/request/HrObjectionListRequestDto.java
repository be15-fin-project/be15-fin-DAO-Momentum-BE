package com.dao.momentum.evaluation.hr.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "팀장이 자신이 작성한 사원의 인사 평가 이의제기 목록 조회 요청 DTO")
public record HrObjectionListRequestDto(
        @Schema(description = "요청자 팀장 사원 ID (토큰에서 채워짐)", example = "1001")
        Long requesterEmpId,

        @Schema(description = "이의제기 상태 ID", example = "1", allowableValues = {"1", "2", "3"}, defaultValue = "null", nullable = true)
        Integer statusId,

        @Schema(description = "평가 회차 번호", example = "2", nullable = true)
        Integer roundNo,

        @Schema(description = "이의제기 생성일 시작일 (yyyy-MM-dd)", example = "2025-06-01", nullable = true)
        String startDate,

        @Schema(description = "이의제기 생성일 종료일 (yyyy-MM-dd)", example = "2025-06-30", nullable = true)
        String endDate,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {

    // 기본값 처리
    public HrObjectionListRequestDto {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
