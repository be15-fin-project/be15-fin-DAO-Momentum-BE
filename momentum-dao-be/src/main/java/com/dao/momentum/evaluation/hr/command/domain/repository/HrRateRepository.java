package com.dao.momentum.evaluation.hr.command.domain.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;

import java.util.Optional;

public interface HrRateRepository {
    HrRate save(HrRate hrRate);
    Optional<HrRate> findByRoundId(Integer roundId);

    void deleteByRoundId(int roundId);
}