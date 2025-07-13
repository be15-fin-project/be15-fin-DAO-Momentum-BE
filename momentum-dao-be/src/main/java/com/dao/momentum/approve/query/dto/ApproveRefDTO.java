package com.dao.momentum.approve.query.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRefDTO {

    private String empId;
    private String employeeDisplayName;
    private String departmentName;
    private String isConfirmed;

}
