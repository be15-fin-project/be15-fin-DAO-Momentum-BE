package com.dao.momentum.approve.query.dto;

import com.dao.momentum.approve.command.domain.aggregate.IsConfirmed;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRefDTO {

    private String employeeDisplayName;
    private String departmentName;
    private String isConfirmed;

}
