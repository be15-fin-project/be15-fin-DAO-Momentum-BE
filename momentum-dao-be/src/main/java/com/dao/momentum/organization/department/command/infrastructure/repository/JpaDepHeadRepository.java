package com.dao.momentum.organization.department.command.infrastructure.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.DeptHead;
import com.dao.momentum.organization.department.command.domain.repository.DeptHeadRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaDepHeadRepository  extends DeptHeadRepository, JpaRepository<DeptHead, Integer> {

    @Query("SELECT d.parentDeptId FROM Department d WHERE d.deptId = :deptId")
    Optional<Integer> findParentDeptIdByDeptId(@Param("deptId") Integer deptId);

}
