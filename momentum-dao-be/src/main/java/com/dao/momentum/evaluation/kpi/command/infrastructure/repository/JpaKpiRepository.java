package com.dao.momentum.evaluation.kpi.command.infrastructure.repository;

import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface JpaKpiRepository extends JpaRepository<Kpi, Long>, KpiRepository {
    @Override
    Optional<Kpi> findById(Long id);

    @Override
    void delete(Kpi kpi);

}
