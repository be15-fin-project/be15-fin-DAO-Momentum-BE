package com.dao.momentum.organization.employee.query.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AppointSearchRequest {
    private Integer deptId;

    private Integer positionId;

    private Long empId;

    private String empName;

    private AppointType type;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private Order order;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;
}
