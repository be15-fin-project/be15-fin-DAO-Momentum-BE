package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRecords;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRecordsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEmployeeRecordsRepository extends EmployeeRecordsRepository, JpaRepository<EmployeeRecords, Long> {
}
