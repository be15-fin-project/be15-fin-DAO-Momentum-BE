package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointCreateRequest {
    private long empId;

    private int positionId;

    private Integer deptId;

    @NotNull(message = "발령 유형은 필수 항목입니다.")
    private AppointType type;

//    @NotNull(message = "발령일은 필수 항목입니다.")
//    private LocalDate appointDate;
}
