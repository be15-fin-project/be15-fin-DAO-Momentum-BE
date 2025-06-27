package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "출퇴근 정정 결재 request")
public class WorkCorrectionRequest {

    @NotNull(message="정정하고자 하는 근무 번호는 반드시 선택해야 합니다.")
    @Schema(description = "정정 근무 ID", example = "1")
    private final Long workId;

    @NotNull(message="기존 출근 일시는 반드시 작성해야 합니다.")
    @Schema(description = "기존 출근 일시")
    private final LocalDateTime beforeStartAt;

    @NotNull(message="기존 퇴근 일시는 반드시 작성해야 합니다.")
    @Schema(description = "기존 퇴근 일시")
    private final LocalDateTime beforeEndAt;

    @NotNull(message="수정 출근 일시는 반드시 작성해야 합니다.")
    @Schema(description = "수정 출근 일시")
    private final LocalDateTime afterStartAt;

    @NotNull(message="수정 퇴근 일시는 반드시 작성해야 합니다.")
    @Schema(description = "수정 퇴근 일시")
    private final LocalDateTime afterEndAt;

    @NotNull(message="출퇴근 정정 사유는 반드시 작성해야 합니다.")
    @Schema(description = "출퇴근 정정 사유")
    private final String reason;

}
