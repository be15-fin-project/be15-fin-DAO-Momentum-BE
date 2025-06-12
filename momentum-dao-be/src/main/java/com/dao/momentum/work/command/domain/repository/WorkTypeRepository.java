package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.WorkType;

import java.util.Optional;

public interface WorkTypeRepository {

    Optional<WorkType> findByTypeName(String work);
}
