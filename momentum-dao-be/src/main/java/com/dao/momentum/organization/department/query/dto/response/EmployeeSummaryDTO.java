package com.dao.momentum.organization.department.query.dto.response;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class EmployeeSummaryDTO {
    private Long empId;
    private String name;
    private String status;
    private String positionName;
}
