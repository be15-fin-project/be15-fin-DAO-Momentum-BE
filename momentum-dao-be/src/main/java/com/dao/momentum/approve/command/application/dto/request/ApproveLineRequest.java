package com.dao.momentum.approve.command.application.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.IsRequiredAll;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재선 request")
public class ApproveLineRequest {

    @NotNull(message = "상태 아이디는 null일 수 없습니다.")
    @Schema(description = "결재선 상태(대기, 승인, 반려) 아이디", example = "1")
    private final Integer statusId;

    @NotNull(message = "결재선 순서는 null일 수 없습니다.")
    @Schema(description = "결재선 순서", example = "1")
    private final Integer approveLineOrder;

    @Schema(description = "결재선 필수/선택 여부")
    private final IsRequiredAll isRequiredAll;

    @Schema(description = "결재선에 지정된 결재자 목록")
    private final List<ApproveLineListRequest> approveLineList;
}
