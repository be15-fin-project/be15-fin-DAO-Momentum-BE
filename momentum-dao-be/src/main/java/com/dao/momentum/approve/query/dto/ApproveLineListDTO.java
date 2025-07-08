package com.dao.momentum.approve.query.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveLineListDTO {

    private Long approveLineId;
    private Long approveLineListId;
    private Long empId;
    private String statusType;
    private String employeeDisplayName;
    private String departmentName;
    private String reason;

}
