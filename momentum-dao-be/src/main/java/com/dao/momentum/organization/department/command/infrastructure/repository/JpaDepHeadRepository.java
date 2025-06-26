package com.dao.momentum.organization.department.command.infrastructure.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.DeptHead;
import com.dao.momentum.organization.department.command.domain.repository.DeptHeadRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDepHeadRepository  extends DeptHeadRepository, JpaRepository<DeptHead, Integer> {
}
