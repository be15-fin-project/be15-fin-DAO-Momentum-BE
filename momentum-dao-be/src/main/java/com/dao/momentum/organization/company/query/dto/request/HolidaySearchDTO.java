package com.dao.momentum.organization.company.query.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidaySearchDTO {
    private Integer year;
    private Integer month;
    private Integer page;
    private Integer size;

    public static HolidaySearchDTO from(HolidaySearchRequest request){
        return HolidaySearchDTO.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .page(request.getPage())
                .size(request.getSize())
                .build();
    }

    public int getPage() {
        return page != null ? page : 1;
    }

    public int getSize() {
        return size != null ? size : 10;
    }

    public int getOffset() {
        return (getPage() - 1) * getSize();
    }
}

