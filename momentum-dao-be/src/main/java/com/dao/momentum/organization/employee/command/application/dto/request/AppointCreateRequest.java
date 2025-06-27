package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AppointCreateRequest {
    private long empId;

    private int positionId;

    private int deptId;

    @NotNull
    private AppointType type;

    @NotNull
    private LocalDate appointDate;
}
