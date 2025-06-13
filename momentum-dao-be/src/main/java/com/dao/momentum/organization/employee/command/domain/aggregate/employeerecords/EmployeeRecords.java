package com.dao.momentum.organization.employee.command.domain.aggregate.employeerecords;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    private RecordType type;

    private String organization;

    private LocalDate startDate;

    private LocalDate endDate;

    private String name;
}
