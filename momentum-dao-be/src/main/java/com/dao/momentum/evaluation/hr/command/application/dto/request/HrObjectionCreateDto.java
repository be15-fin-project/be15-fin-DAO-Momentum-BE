package com.dao.momentum.evaluation.hr.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "인사 평가 이의제기 생성 내부 DTO")
public class HrObjectionCreateDto {

    @Schema(description = "평가 결과 ID", example = "123")
    private Long resultId;

    @Schema(description = "사원 ID", example = "1001")
    private Long writerId;

    @Schema(description = "이의제기 사유", example = "성과 반영이 누락되어 정확하지 않은 평가입니다.")
    private String reason;
}