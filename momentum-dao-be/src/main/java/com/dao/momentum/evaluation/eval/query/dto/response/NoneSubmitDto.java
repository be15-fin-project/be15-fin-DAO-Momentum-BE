package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "미제출 사원 정보 DTO")
public class NoneSubmitDto {

    @Schema(description = "사원 ID", example = "14")
    private Long empId;

    @Schema(description = "사원 번호", example = "20250001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김여진")
    private String name;

    @Schema(description = "부서 ID", example = "12")
    private Long deptId;

    @Schema(description = "부서명", example = "영업팀")
    private String deptName;

}
