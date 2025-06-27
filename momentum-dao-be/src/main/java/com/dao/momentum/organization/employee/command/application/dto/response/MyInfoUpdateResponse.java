package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "본인 정보 수정 응답 객체")
public class MyInfoUpdateResponse {
    @Schema(description = "사원 ID", example = "1")
    private Long empId;

    @Schema(description = "응답 메시지", example = "정보 수정 완료")
    private String message;
}
