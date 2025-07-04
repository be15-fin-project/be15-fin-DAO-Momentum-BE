package com.dao.momentum.retention.prospect.query.dto.response;

import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "근속 전망 항목 DTO")
public class RetentionForecastItemDto {

    @Schema(description = "근속 전망 Id", example = "31")
    private Long retentionId;

    @Schema(description = "사번", example = "20240001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김현우")
    private String empName;

    @Schema(description = "부서 이름", example = "인사팀")
    private String deptName;

    @Schema(description = "직급 이름", example = "과장")
    private String positionName;

    @Schema(description = "근속 지수 등급 (예: 탁월, 우수, 보통)", example = "우수")
    private String retentionGrade;

    @Schema(description = "안정성 유형 필터", example = "주의형", allowableValues = {"안정형", "주의형", "불안정형"})
    private StabilityType stabilityType;

    @Schema(description = "근속 평가 요약 정보", example = "직무:우수, 관계:보통")
    private String summaryComment;

    @Schema(description = "근속 전망 회차 번호", example = "3")
    private int roundNo;
}
