package com.dao.momentum.work.command.infrastructure.repository;

import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWorkTypeRepository extends WorkTypeRepository, JpaRepository<WorkType, Integer> {
}
