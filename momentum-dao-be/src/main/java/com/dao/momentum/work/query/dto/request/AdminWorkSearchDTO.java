package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminWorkSearchDTO {
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

    private int getOffset() {
        return (page - 1) * size;
//        int page = getPage() == null ? 1 : getPage();
//        return (page - 1) * getLimit();
    }

    private int getLimit() {
        return getSize() == null ? 10 : getSize();
    }
}
