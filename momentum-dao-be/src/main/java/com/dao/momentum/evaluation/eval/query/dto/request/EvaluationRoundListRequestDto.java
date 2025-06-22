package com.dao.momentum.evaluation.eval.query.dto.request;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "다면 평가 회차 목록 조회 요청 DTO")
public class EvaluationRoundListRequestDto {

    @Schema(description = "조회 시작일", example = "2025-07-01", type = "string", format = "date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "조회 종료일", example = "2025-07-31", type = "string", format = "date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Schema(
            description = "평가 상태",
            example = "IN_PROGRESS",
            allowableValues = {"BEFORE", "IN_PROGRESS", "DONE"}
    )
    private EvaluationRoundStatus status;

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
