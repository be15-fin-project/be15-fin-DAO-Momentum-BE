package com.dao.momentum.retention.prospect.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 회차 생성 응답 DTO")
public record RetentionForecastCreateResponse(

        @Schema(description = "생성된 회차 ID", example = "3")
        Integer roundId,

        @Schema(description = "생성된 회차 번호", example = "3")
        Integer roundNo,

        @Schema(description = "처리 결과 메시지", example = "근속 전망 회차가 성공적으로 생성되었습니다.")
        String message

) {}
