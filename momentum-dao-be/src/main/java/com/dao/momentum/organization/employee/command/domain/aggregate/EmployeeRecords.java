package com.dao.momentum.organization.employee.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long recordId;

    private long empId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String organization;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private String name;
}
