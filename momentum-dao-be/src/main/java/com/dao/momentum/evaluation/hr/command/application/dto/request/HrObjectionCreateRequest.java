package com.dao.momentum.evaluation.hr.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "인사 평가 이의제기 요청 DTO")
public class HrObjectionCreateRequest {

    @NotBlank(message = "이의제기 사유는 필수입니다.")
    @Schema(description = "이의제기 사유", example = "평가 기준이 불명확하고 실제 성과가 반영되지 않았습니다.")
    private String reason;

    public HrObjectionCreateDto toDto(Long evaluationId, Long empId) {
        return HrObjectionCreateDto.builder()
                .resultId(evaluationId)
                .writerId(empId)
                .reason(this.reason)
                .build();
    }

}
