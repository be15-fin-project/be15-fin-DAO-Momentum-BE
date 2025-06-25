package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 양식 상세 응답 DTO")
public class EvalFormDetailResultDto {

    @Schema(description = "평가 양식명")
    private String formName;

    @Schema(description = "요인별 문항 정보")
    private List<FactorDto> factors;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "요인 정보")
    public static class FactorDto {

        @Schema(description = "요인 이름")
        private String propertyName;

        @Schema(description = "문항 목록")
        private List<PromptDto> prompts;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "문항 정보")
    public static class PromptDto {

        @Schema(description = "문항 내용")
        private String content;

        @Schema(description = "긍정 문항 여부")
        private boolean isPositive;
    }
}
