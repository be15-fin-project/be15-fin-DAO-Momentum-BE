package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "인사 발령 등록 응답 객체")
public class AppointCreateResponse {
    @Schema(description = "인사 발령 ID", example = "1")
    private long appointId;

    @Schema(description = "응답 메시지", example = "발령 등록 완료")
    private String message;
}
