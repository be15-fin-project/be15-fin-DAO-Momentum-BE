package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.organization.employee.query.dto.request.AppointType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "인사 발령 DTO")
public class AppointDTO {
    @Schema(description = "인사 발령 ID", example = "1")
    private long appointId;

    @Schema(description = "인사 발령 대상자 ID", example = "1")
    private long empId;

    @Schema(description = "인사 발령 대상자 사번", example = "20250001")
    private String empNo;

    @Schema(description = "인사 발령 대상자 이름", example = "홍길동")
    private String empName;

    @Schema(description = "기존 직위 ID", example = "8")
    private int beforePosition;

    @Schema(description = "기존 직위 이름", example = "사원")
    private String beforePositionName;

    @Schema(description = "발령 직위 ID", example = "7")
    private int afterPosition;

    @Schema(description = "발령 직위 이름", example = "대리")
    private String afterPositionName;

    @Schema(description = "기존 부서 ID", example = "11")
    private int beforeDepartment;

    @Schema(description = "기존 부서명", example = "마게팅팀")
    private String beforeDeptName;

    @Schema(description = "발령 부서 ID", example = "12")
    private int afterDepartment;

    @Schema(description = "발령 부서 이름", example = "기획팀")
    private String afterDeptName;

    @Schema(description = "발령 유형")
    private AppointType type;

    @Schema(description = "발령일")
    private LocalDate appointDate;
}
