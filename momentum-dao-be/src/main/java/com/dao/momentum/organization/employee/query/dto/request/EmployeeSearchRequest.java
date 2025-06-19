package com.dao.momentum.organization.employee.query.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EmployeeSearchRequest {
    private Integer deptId;

//    private String deptName;

    private Integer positionId;

//    private String positionName;

    private UserRoleName userRole;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private SortBy sortBy;

    private Order order;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    public static EmployeeSearchDTO fromRequest(EmployeeSearchRequest employeeSearchRequest) {
        return EmployeeSearchDTO.builder()
                .deptId(employeeSearchRequest.getDeptId())
                .positionId(employeeSearchRequest.getPositionId())
                .userRole(employeeSearchRequest.getUserRole())
                .searchStartDate(employeeSearchRequest.getSearchStartDate())
                .searchEndDate(employeeSearchRequest.getSearchEndDate())
                .sortBy(employeeSearchRequest.getSortBy())
                .order(employeeSearchRequest.getOrder())
                .page(employeeSearchRequest.getPage() == null ?
                        1 : employeeSearchRequest.getPage())
                .size(employeeSearchRequest.getSize() == null ?
                        10 : employeeSearchRequest.getSize())
                .build();
    }
}
