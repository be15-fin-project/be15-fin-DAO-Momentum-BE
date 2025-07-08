package com.dao.momentum.approve.query.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeLeaderDto {

    private Long teamLeaderId;
    private String teamLeaderName;
    private String leaderDeptName;

}
