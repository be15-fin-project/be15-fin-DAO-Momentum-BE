package com.dao.momentum.organization.employee.command.application.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;
import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AppointDTO {
    private long appointId;

    private long empId;

    private int beforePosition;

    private int afterPosition;

    private int beforeDepartment;

    private int afterDepartment;

    private AppointType type;

    private LocalDate appointDate;

    public static AppointDTO from(Appoint appoint) {
        return AppointDTO.builder()
                .appointId(appoint.getAppointId())
                .empId(appoint.getEmpId())
                .beforePosition(appoint.getBeforePosition())
                .afterPosition(appoint.getAfterPosition())
                .beforeDepartment(appoint.getBeforeDepartment())
                .afterDepartment(appoint.getAfterDepartment())
                .type(appoint.getType())
                .appointDate(appoint.getAppointDate())
                .build();
    }

}
