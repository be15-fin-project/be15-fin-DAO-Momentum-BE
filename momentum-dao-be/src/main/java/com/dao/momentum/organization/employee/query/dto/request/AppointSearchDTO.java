package com.dao.momentum.organization.employee.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AppointSearchDTO {
    private Integer deptId;

    private Integer positionId;

    private Long empId;

    private String empName;

    private AppointType type;

    private LocalDate searchStartDate;

    private LocalDate searchEndDate;

    private Order order;

    private int page;

    private int size;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }

    public static AppointSearchDTO fromRequest(AppointSearchRequest request) {
        return AppointSearchDTO.builder()
                .deptId(request.getDeptId())
                .positionId(request.getPositionId())
                .empId(request.getEmpId())
                .empName(request.getEmpName())
                .type(request.getType())
                .order(request.getOrder())
                .searchStartDate(request.getSearchStartDate())
                .searchEndDate(request.getSearchEndDate())
                .page(request.getPage() == null ? 1 : request.getPage())
                .size(request.getSize() == null ? 10 : request.getSize())
                .build();
    }
}
