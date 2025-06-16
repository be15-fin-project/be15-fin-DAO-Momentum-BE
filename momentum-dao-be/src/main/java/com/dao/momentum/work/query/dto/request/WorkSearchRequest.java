package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkSearchRequest {
    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    // EndDate에 지정한 날짜를 포함하려면 LocalDateTime 기준으로 하루 더 있어야 함
    public void addToEndDate() {
        if (rangeEndDate != null) {
            rangeEndDate = rangeEndDate.plusDays(1);
        }
    }

}
