package com.dao.momentum.organization.department.query.dto.response;

import lombok.Getter;

@Getter
public class DepartmentFlatDTO {
    private Integer deptId;
    private Integer parentDeptId;
    private String name;
}
