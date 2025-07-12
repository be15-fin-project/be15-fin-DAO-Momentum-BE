package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "부서별 근속 전망 구간(progress) 분포 DTO")
public record RetentionProgressDistributionDto(

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "부서명", example = "기획팀")
        String deptName,

        @Schema(description = "직위 ID", example = "3")
        Long positionId,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        @Schema(description = "전체 사원 수", example = "23")
        Long empCount,

        @Schema(description = "20% 미만 인원 수", example = "1")
        Long progress20,

        @Schema(description = "40% 미만 인원 수", example = "2")
        Long progress40,

        @Schema(description = "60% 미만 인원 수", example = "5")
        Long progress60,

        @Schema(description = "80% 미만 인원 수", example = "8")
        Long progress80,

        @Schema(description = "100% 인원 수", example = "7")
        Long progress100
) {}
