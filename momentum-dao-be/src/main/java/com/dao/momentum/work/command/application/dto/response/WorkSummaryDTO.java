package com.dao.momentum.work.command.application.dto.response;

import com.dao.momentum.work.command.domain.aggregate.Work;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class WorkSummaryDTO {
    private long workId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private int breakTime;

    private int workTime;

    public static WorkSummaryDTO from(Work work) {

        return WorkSummaryDTO.builder()
                .workId(work.getWorkId())
                .startAt(work.getStartAt())
                .endAt(work.getEndAt())
                .breakTime(work.getBreakTime())
                .workTime((int) work.getWorkTime().toMinutes())
                .build();
    }
}
