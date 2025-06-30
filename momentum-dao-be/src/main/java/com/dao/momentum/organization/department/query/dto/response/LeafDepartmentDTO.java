package com.dao.momentum.organization.department.query.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class LeafDepartmentDTO {
    private Integer deptId;

    private String name;
}
