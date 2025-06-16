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

    private String order;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    private int getOffset() {
        int page = getPage() == null ? 1 : getPage();
        return (page - 1) * getLimit();
    }

    private int getLimit() {
        return getSize() == null ? 10 : getSize();
    }
}
