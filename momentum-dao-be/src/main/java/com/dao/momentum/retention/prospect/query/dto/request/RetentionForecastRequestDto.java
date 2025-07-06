package com.dao.momentum.retention.prospect.query.dto.request;

import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 조회 요청 DTO")
public record RetentionForecastRequestDto(
        @Schema(description = "조회할 회차 번호 (미입력 시 최신 회차로 처리)", example = "3")
        Integer roundNo,

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "부서 ID (필터)", example = "5")
        Integer deptId,

        @Schema(description = "직위 ID (필터)", example = "3")
        Integer positionId,

        @Schema(description = "안정성 유형 필터", example = "주의형", allowableValues = {"안정형", "주의형", "불안정형"})
        StabilityType stabilityType,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {
    // 레코드에서 기본값을 설정할 수 없으므로, 생성자에서 기본값을 설정
    public RetentionForecastRequestDto {
        if (page == null) page = 1;
        if (size == null) size = 10;
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
