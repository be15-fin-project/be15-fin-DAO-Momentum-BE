package com.dao.momentum.organization.employee.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EmployeeRecordsDTO {
    private long recordId;

    private long empId;

    private RecordType type;

    private String organization;

    private LocalDate startDate;

    private LocalDate endDate;

    private String name;
}
