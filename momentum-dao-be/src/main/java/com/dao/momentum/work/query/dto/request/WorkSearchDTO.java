package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkSearchDTO {
    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

    public static WorkSearchDTO fromRequest(WorkSearchRequest request) {
        LocalDate rangeEndDate = request.getRangeEndDate();

        return WorkSearchDTO.builder()
                .rangeStartDate(request.getRangeStartDate())
                // EndDate에 지정한 날짜를 포함하려면 LocalDateTime 기준으로 하루 더 있어야 함
                .rangeEndDate(rangeEndDate == null ?
                        null : rangeEndDate.plusDays(1))
                .build();
    }

}
