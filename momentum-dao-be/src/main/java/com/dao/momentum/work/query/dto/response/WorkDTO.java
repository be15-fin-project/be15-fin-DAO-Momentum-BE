package com.dao.momentum.work.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "근무 기록 DTO")
public class WorkDTO {
    @Schema(description = "근무 기록 ID", example = "1")
    private long workId;

    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "사번", example = "20250001")
    private String empNo;

    @Schema(description = "사원 이름", example = "홍길동")
    private String empName;

    private Integer deptId;

    private String deptName;

    private int positionId;

    private String positionName;

    private int workTime;

    @Schema(description = "근무 유형 ID", example = "3")
    private int typeId;

    @Schema(description = "근무 유형 이름", example = "VACATION")
    private String typeName;

    @Schema(description = "휴가 유형 ID", example = "1")
    private Integer vacationTypeId;

    @Schema(description = "휴가 유형", example = "DAYOFF")
    private String vacationType;

    @Schema(description = "시작 일시")
    private LocalDateTime startAt;

    @Schema(description = "종료 일시")
    private LocalDateTime endAt;

    @Schema(description = "정상 근무 여부")
    private IsNormalWork isNormalWork;

}
