package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "간단한 사원 정보 DTO")
public class EmployeeSimpleDto {

    @Schema(description = "사원 ID", example = "35")
    private Long empId;

    @Schema(description = "사원 번호", example = "20250014")
    private String empNo;

    @Schema(description = "사원 이름", example = "문채윤")
    private String name;

    @Schema(description = "부서 ID", example = "12")
    private Long deptId;

    @Schema(description = "부서명", example = "영업팀")
    private String deptName;
}
