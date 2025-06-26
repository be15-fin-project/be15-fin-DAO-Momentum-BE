package com.dao.momentum.evaluation.hr.command.application.dto.request;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "이의제기 처리 요청 DTO (승인 or 반려)")
public class HrObjectionProcessRequestDto {

    @Schema(description = "처리할 이의 제기 ID", example = "12")
    @NotNull
    private Long objectionId;

    @Schema(description = "승인 여부: true면 승인, false면 반려", example = "true")
    @NotNull
    private Boolean approved;

    @Schema(description = "반려 사유 (승인 아닌 경우 필수)", example = "근거가 부족함")
    private String rejectReason;

    @Schema(description = "요인별 점수 목록 (승인 시 필수)")
    private List<EvalFactorScoreDto> factorScores;


    @Getter
    @NoArgsConstructor
    @Schema(description = "요인별 점수 입력 DTO")
    public static class EvalFactorScoreDto {

        @Schema(description = "평가 요인 ID", example = "1")
        @NotNull
        private Integer propertyId;

        @Schema(description = "점수 (0~100)", example = "80")
        @Min(0)
        @Max(100)
        private Integer score;

        public static EvalScore toEntity(Long resultId, Integer propertyId, Integer score) {
            return EvalScore.builder()
                    .resultId(resultId)
                    .propertyId(propertyId)
                    .score(score)
                    .build();
        }

    }

}
