package com.dao.momentum.organization.department.query.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class DepartmentNodeDTO {
    private Integer deptId;
    private Integer parentDeptId;
    private String deptName;
    private List<EmployeeSummaryDTO> employees;
    private List<DepartmentNodeDTO> children = new ArrayList<>();;
}
