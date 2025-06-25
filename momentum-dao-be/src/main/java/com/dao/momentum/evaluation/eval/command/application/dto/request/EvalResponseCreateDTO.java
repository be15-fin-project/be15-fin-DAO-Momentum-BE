package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "다면 평가 응답 생성 요청 DTO")
public class EvalResponseCreateDTO {

    @Schema(description = "평가 회차 ID", example = "3")
    private Integer roundId;

    @Schema(description = "평가 양식 ID", example = "7")
    private Integer formId;

    @Schema(description = "평가자 사원 ID", example = "10001")
    private Long evalId;

    @Schema(description = "피평가자 사원 ID", example = "10002")
    private Long targetId;

    @Schema(description = "종합 점수", example = "87")
    private Integer score;

    @Schema(description = "평가 사유", example = "업무 책임감이 뛰어나며 협업도 우수함.")
    private String reason;
}
