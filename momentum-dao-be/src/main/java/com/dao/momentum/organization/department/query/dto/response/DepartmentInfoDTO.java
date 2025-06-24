package com.dao.momentum.organization.department.query.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class DepartmentInfoDTO {
    private Integer deptId;
    private Integer parentDeptId;
    private String name;
    private List<DepartmentInfoDTO> childDept;
}
