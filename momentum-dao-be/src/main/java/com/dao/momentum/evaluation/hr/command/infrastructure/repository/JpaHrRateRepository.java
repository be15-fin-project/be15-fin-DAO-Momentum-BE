package com.dao.momentum.evaluation.hr.command.infrastructure.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaHrRateRepository extends JpaRepository<HrRate, Integer>, HrRateRepository {
    Optional<HrRate> findByRoundId(Integer roundId);

    void deleteByRoundId(int roundId);
}