package com.dao.momentum.work.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "당일 출근 응답 객체")
public class AttendanceResponse {
    @Schema(description = "출근 DTO")
    private AttendanceDTO Attendance;

    @Schema(description = "사원 ID", example = "1")
    private long empId;
}
