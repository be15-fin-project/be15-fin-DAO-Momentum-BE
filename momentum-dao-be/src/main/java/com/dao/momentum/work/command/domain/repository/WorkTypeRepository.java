package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;

import java.util.Optional;

public interface WorkTypeRepository {

    Optional<WorkType> findByTypeName(String work);

    Optional<WorkType> findById(int typeId);
}
