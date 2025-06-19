package com.dao.momentum.organization.employee.command.domain.aggregate;

import com.dao.momentum.organization.employee.query.dto.response.RecordType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class EmployeeRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long recordId;

    private long empId;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private String organization;

    private LocalDate startDate;

    private LocalDate endDate;

    private String name;
}
