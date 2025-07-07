package com.dao.momentum.evaluation.hr.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "인사 평가 이의 제기 삭제 응답 DTO")
public record HrObjectionDeleteResponse(
        @Schema(description = "삭제된 이의 제기 ID", example = "12")
        Long objectionId,

        @Schema(description = "삭제 성공 메시지", example = "인사 평가 이의 제기가 성공적으로 삭제되었습니다.")
        String message
) {
}
