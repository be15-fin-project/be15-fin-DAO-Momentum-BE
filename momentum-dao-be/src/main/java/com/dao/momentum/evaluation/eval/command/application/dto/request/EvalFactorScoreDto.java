package com.dao.momentum.evaluation.eval.command.application.dto.request;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "요인별 점수 DTO")
public class EvalFactorScoreDto {

    @Schema(description = "요인 ID", example = "101")
    @NotNull
    private Integer propertyId;

    @Schema(description = "점수", example = "17")
    @NotNull
    private Integer score;


}
