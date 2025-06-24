package com.dao.momentum.organization.department.query.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class DepartmentInfoDTO {
    private Integer deptId;
    private Integer parentDeptId;
    private String name;
    private List<DepartmentInfoDTO> childDept;
}
