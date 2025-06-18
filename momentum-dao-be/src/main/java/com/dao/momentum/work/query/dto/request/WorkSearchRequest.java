package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkSearchRequest {
    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    public WorkSearchDTO toDTO() {
        return WorkSearchDTO.builder()
                .rangeStartDate(this.rangeStartDate)
                // EndDate에 지정한 날짜를 포함하려면 LocalDateTime 기준으로 하루 더 있어야 함
                .rangeEndDate(this.rangeEndDate == null ?
                        null : this.rangeEndDate.plusDays(1))
                .build();
    }

}
