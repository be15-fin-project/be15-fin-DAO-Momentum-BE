package com.dao.momentum.evaluation.query.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class KpiStatisticsRequestDto {
    private Integer year;
    private Integer month;
    private Long deptId;
    private Long empId;
    private String userRole;
}
