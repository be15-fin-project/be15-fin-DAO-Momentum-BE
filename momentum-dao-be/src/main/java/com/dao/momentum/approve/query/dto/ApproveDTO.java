package com.dao.momentum.approve.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveDTO {

    private Long approveId;
    private Long parentApproveId;
    private String statusType;
    private Long empId;
    private String approveTitle;
    private String approveType;
    private LocalDateTime createAt;
    private LocalDateTime completeAt;
    private String employeeName;
    private String departmentName;

}
