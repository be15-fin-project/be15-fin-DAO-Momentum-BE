package com.dao.momentum.organization.employee.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeRecordsUpdateResponse {
    private List<Long> insertedIds;

    private List<Long> deletedIds;

    private String message;
}
