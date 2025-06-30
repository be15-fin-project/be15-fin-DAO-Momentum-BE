package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사원 인사 정보 수정 응답 객체")
public class EmployeeRecordsUpdateResponse {
    @Schema(description = "추가된 인사 정보 ID 목록")
    private List<Long> insertedIds;

    @Schema(description = "삭제된 인사 정보 ID 목록")
    private List<Long> deletedIds;

    @Schema(description = "응답 메시지", example = "사원 인사 정보 수정 완료")
    private String message;
}
