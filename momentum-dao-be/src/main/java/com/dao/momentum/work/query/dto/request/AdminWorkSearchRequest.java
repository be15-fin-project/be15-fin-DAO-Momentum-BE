package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminWorkSearchRequest {
    private String empNo;

    private String empName;

    private String deptName;

    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    private String typeName;

    private Integer vacationTypeId;

    private Order order;

    private Integer page;

    private Integer size;


}
