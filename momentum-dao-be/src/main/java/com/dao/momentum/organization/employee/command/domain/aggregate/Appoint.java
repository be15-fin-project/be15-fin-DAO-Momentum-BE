package com.dao.momentum.organization.employee.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Appoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long appointId;

    private long empId;

    private int beforePosition;

    private int afterPosition;

    private int beforeDepartment;

    private int afterDepartment;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AppointType type;

    @NotNull
    private LocalDate appointDate;

    @Builder
    public Appoint(long empId, int beforePosition, int afterPosition, int beforeDepartment, int afterDepartment, AppointType type, LocalDate appointDate) {
        this.empId = empId;
        this.beforePosition = beforePosition;
        this.afterPosition = afterPosition;
        this.beforeDepartment = beforeDepartment;
        this.afterDepartment = afterDepartment;
        this.type = type;
        this.appointDate = appointDate;
    }

}
