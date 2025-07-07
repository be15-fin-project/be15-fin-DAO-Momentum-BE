package com.dao.momentum.work.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "당일 출근 응답 객체")
public class AttendanceResponse {
    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "출근 DTO")
    private AttendanceDTO attendance;

    private AttendanceDTO amHalfDayoff;

    private AttendanceDTO pmHalfDayoff;

    private AttendanceDTO dayoff;

    private AttendanceDTO vacation;

    private AttendanceDTO approvedWork;

    private boolean isWeekend;

    private boolean isHoliday;
}
