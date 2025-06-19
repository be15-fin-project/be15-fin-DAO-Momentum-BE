package com.dao.momentum.organization.employee.query.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EmployeeSearchDTO {
    private Integer deptId;

    private Integer positionId;

    private UserRoleName userRole;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private SortBy sortBy;

    private Order order;

    private int page;

    private int size;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}
