package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 지원 엑셀 다운로드용 DTO")
public record RetentionSupportExcelDto(

    @Schema(description = "사번", example = "EMP001")
    String employeeNo,

    @Schema(description = "이름", example = "홍길동")
    String employeeName,

    @Schema(description = "회차 번호", example = "2024-03")
    String roundNo,

    @Schema(description = "부서명", example = "인사팀")
    String deptName,

    @Schema(description = "근속 전망 점수", example = "82.5")
    Double retentionScore,

    @Schema(description = "직무 만족도", example = "80.0")
    Double jobSatisfaction,

    @Schema(description = "보상 만족도", example = "75.0")
    Double compensationSatisfaction,

    @Schema(description = "관계 만족도", example = "78.0")
    Double relationSatisfaction,

    @Schema(description = "성장 만족도", example = "70.0")
    Double growthSatisfaction,

    @Schema(description = "근속 연수", example = "3.5")
    Double tenure,

    @Schema(description = "워라밸 만족도", example = "85.0")
    Double wlbSatisfaction
) {}
