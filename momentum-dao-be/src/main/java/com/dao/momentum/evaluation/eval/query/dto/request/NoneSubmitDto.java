package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "미제출 사원 정보 DTO")
@Builder
public record NoneSubmitDto(

        @Schema(description = "사원 ID", example = "14")
        Long empId,

        @Schema(description = "사원 번호", example = "20250001")
        String empNo,

        @Schema(description = "사원 이름", example = "김여진")
        String name,

        @Schema(description = "부서 ID", example = "12")
        Long deptId,

        @Schema(description = "부서명", example = "영업팀")
        String deptName
) { }
