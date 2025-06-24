package com.dao.momentum.evaluation.kpi.command.domain.repository;

import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
}
