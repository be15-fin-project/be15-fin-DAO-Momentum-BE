package com.dao.momentum.organization.department.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class DepartmentUpdateRequest {
    @NotNull
    private Integer deptId;

    private Integer parentDeptId;

    @NotBlank
    private String name;

    private Long deptHeadId;

    @NotBlank
    @Pattern(
            regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
            message = "유효한 휴대폰 번호 형식이 아닙니다. 예: 010-1234-5678"
    )

    private String contact;
}
