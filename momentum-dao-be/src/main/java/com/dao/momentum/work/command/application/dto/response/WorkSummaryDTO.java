package com.dao.momentum.work.command.application.dto.response;

import com.dao.momentum.work.command.domain.aggregate.Work;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "출퇴근 기록 DTO")
public class WorkSummaryDTO {
    @Schema(description = "출퇴근 기록 ID")
    private long workId;

    @Schema(description = "기록할 출근 일시")
    private LocalDateTime startAt;

    @Schema(description = "기록할 퇴근 일시")
    private LocalDateTime endAt;

    @Schema(description = "휴게시간 (분 단위)", example = "60")
    private int breakTime;

    @Schema(description = "근무 시간 (분 단위)", example = "480")
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
