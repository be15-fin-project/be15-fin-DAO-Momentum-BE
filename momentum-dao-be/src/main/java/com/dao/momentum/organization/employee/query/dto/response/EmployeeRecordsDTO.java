package com.dao.momentum.organization.employee.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "사원 인사 정보 DTO")
public class EmployeeRecordsDTO {
    @Schema(description = "사원 인사 정보 ID", example = "1")
    private long recordId;

    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "종류")
    private RecordType type;

    @Schema(description = "기관", example = "국립대학교")
    private String organization;

    @Schema(description = "시작일")
    private LocalDate startDate;

    @Schema(description = "종료일")
    private LocalDate endDate;

    @Schema(description = "내용", example = "성적 우수상")
    private String name;
}
