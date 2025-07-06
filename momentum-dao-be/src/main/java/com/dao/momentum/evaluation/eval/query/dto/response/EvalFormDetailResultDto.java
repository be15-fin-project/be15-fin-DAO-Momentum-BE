package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "평가 양식 상세 응답 DTO")
@Builder
public record EvalFormDetailResultDto(

        @Schema(description = "평가 양식명")
        String formName,

        @Schema(description = "요인별 문항 정보")
        List<FactorDto> factors
) {

    @Schema(description = "요인 정보")
    @Builder
    public record FactorDto(

            @Schema(description = "요인 이름")
            String propertyName,

            @Schema(description = "문항 목록")
            List<PromptDto> prompts
    ) { }

    @Schema(description = "문항 정보")
    @Builder
    public record PromptDto(

            @Schema(description = "문항 내용")
            String content,

            @Schema(description = "긍정 문항 여부")
            boolean isPositive
    ) { }
}
