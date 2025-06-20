package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRecords;

import java.util.List;

public interface EmployeeRecordsRepository {
    void deleteAllByRecordIdIn(List<Long> idsToDelete);

    EmployeeRecords save(EmployeeRecords rec);

    List<EmployeeRecords> findAllByRecordIdIn(List<Long> idsToFind);

}
