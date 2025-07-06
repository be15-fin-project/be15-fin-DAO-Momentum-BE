package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 상세 조회 요청 DTO")
public record RetentionSupportDetailRequestDto(
        @Schema(description = "회차 ID", example = "12", required = true)
        Integer roundId,

        @Schema(description = "사원 ID", example = "10023", required = true)
        Long empId
) {
}
