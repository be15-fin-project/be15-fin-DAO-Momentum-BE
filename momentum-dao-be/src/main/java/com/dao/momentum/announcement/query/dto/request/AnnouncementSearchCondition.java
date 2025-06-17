package com.dao.momentum.announcement.query.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementSearchCondition {
    private String title;
    private String name;
    private Integer deptId;
    private LocalDate startDate;
    private LocalDate endDate;
    private SortDirection sortDirection;
    private int offset;
    private int limit;


    public static AnnouncementSearchCondition from(AnnouncementSearchRequest request) {
        return AnnouncementSearchCondition.builder()
            .title(request.getTitle())
            .name(request.getName())
            .deptId(request.getDeptId())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .sortDirection(request.getSortDirection())
            .offset(request.getOffset())
            .limit(request.getLimit())
            .build();
    }
}