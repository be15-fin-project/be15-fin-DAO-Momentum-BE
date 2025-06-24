package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminWorkSearchRequest {
    private Long empNo;

    private String empName;

    private String deptName;

    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    private String typeName;

    private Integer vacationTypeId;

    private Order order;

    private Integer page;

    private Integer size;

    public AdminWorkSearchDTO toDTO() {
        return AdminWorkSearchDTO.builder()
                .empNo(this.empNo)
                .empName(this.empName)
                .deptName(this.deptName)
                .rangeStartDate(this.rangeStartDate)
                .rangeEndDate(this.rangeEndDate == null ?
                        null : this.rangeEndDate.plusDays(1))
                .typeName(this.typeName)
                .order(this.order)
                .page(this.page == null ? 1 : this.page)
                .size(this.size == null ? 10 : this.size)
                .build();
    }
}
