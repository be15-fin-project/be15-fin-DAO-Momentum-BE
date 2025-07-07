package com.dao.momentum.approve.command.application.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.IsConfirmed;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "참조자 request")
public class ApproveRefRequest {

    @NotBlank(message = "사원 id는 null일 수 없습니다.")
    @Schema(description = "참조 사원 ID", example = "1")
    private final Long empId;

    @NotNull
    @Schema(description = "참조 여부")
    private final IsConfirmed isConfirmed;

}
