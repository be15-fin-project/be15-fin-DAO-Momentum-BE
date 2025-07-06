package com.dao.momentum.evaluation.eval.query.dto.request;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 목록 조회 요청 DTO")
@Builder
public record EvaluationRoundListRequestDto(

        @Schema(description = "조회 시작일", example = "2025-07-01", type = "string", format = "date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @Schema(description = "조회 종료일", example = "2025-07-31", type = "string", format = "date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        @Schema(
                description = "평가 상태",
                example = "IN_PROGRESS",
                allowableValues = {"BEFORE", "IN_PROGRESS", "DONE"}
        )
        EvaluationRoundStatus status,

        @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {

    
    public EvaluationRoundListRequestDto {
        if (page == null) { 
            page = 1;
        }
        if (size == null) { // Default value is 10 if not provided
            size = 10;
        }
    }

    // Method to calculate offset
    public int getOffset() {
        return (page - 1) * size;
    }
}
