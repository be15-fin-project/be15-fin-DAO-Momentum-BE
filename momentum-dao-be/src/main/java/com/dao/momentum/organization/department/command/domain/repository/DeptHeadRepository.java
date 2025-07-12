package com.dao.momentum.organization.department.command.domain.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.DeptHead;

import java.util.Optional;


public interface DeptHeadRepository {
    Optional<DeptHead> findByDeptId(Integer deptId);

    Integer deleteByDeptId(int deptId);

    DeptHead save(DeptHead deptHead);

    Optional<DeptHead> findByEmpId(Long empId);

}
