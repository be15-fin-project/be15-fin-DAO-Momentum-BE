package com.dao.momentum.work.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@Schema(description = "출근 등록 응답 객체")
public class WorkStartResponse {
    @Schema(description = "출퇴근 기록")
    private WorkSummaryDTO workSummaryDTO;

    @Schema(description = "응답 메시지", example = "출근 등록 완료")
    private String message;
}
