package com.dao.momentum.retention.query.dto.request;

import com.dao.momentum.retention.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "근속 전망 조회 요청 DTO")
public class RetentionForecastRequestDto {

    @Schema(description = "조회할 회차 번호 (미입력 시 최신 회차로 처리)", example = "3")
    private Integer roundNo;

    @Schema(description = "부서 ID (필터)", example = "5")
    private Integer deptId;

    @Schema(description = "직위 ID (필터)", example = "3")
    private Integer positionId;

    @Schema(description = "안정성 유형 필터", example = "주의형", allowableValues = {"안정형", "주의형", "불안정형"})
    private StabilityType stabilityType;

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
