package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "KPI 진척도 최신화 요청 DTO")
public class KpiProgressUpdateRequest {

    @NotNull(message = "진척도는 필수 입력 값입니다.")
    @Min(value = 0, message = "진척도는 0 이상이어야 합니다.")
    @Max(value = 100, message = "진척도는 100 이하여야 합니다.")
    @Schema(description = "KPI 진척도 (0~100)", example = "75")
    private Integer progress;
}
