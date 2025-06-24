package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.organization.employee.query.dto.request.AppointType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AppointDTO {
    private long appointId;

    private long empId;

    private String empNo;

    private String empName;

    private int beforePosition;

    private String beforePositionName;

    private int afterPosition;

    private String afterPositionName;

    private int beforeDepartment;

    private String beforeDeptName;

    private int afterDepartment;

    private String afterDeptName;

    private AppointType type;

    private LocalDate appointDate;
}
