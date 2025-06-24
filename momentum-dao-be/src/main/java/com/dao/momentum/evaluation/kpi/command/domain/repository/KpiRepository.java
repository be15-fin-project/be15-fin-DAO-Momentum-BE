package com.dao.momentum.evaluation.kpi.command.domain.repository;

import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;

import java.util.Optional;

public interface KpiRepository {
    Kpi save(Kpi kpi);
    Optional<Kpi> findById(Long id);
}