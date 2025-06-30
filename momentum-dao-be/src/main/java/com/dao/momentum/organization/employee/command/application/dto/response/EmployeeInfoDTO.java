package com.dao.momentum.organization.employee.command.application.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "수정된 사원 기본 정보")
public class EmployeeInfoDTO {
    @Schema(description = "수정 대상 사원 ID", example = "1")
    private Long empId;

    @Schema(description = "사번", example = "20250001")
    private String empNo;

    @Schema(description = "이메일 주소", example ="employee@example.com")
    private String email;

    @Schema(description = "재직 상태")
    private Status status;

    public static EmployeeInfoDTO fromEmployee(Employee employee) {

        return EmployeeInfoDTO.builder()
                .empId(employee.getEmpId())
                .empNo(employee.getEmpNo())
                .email(employee.getEmail())
                .status(employee.getStatus())
                .build();
    }
}
