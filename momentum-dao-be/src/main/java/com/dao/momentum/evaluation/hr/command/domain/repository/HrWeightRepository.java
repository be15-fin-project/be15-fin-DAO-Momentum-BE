package com.dao.momentum.evaluation.hr.command.domain.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;

import java.util.Optional;

public interface HrWeightRepository {
    HrWeight save(HrWeight hrWeight);
    Optional<HrWeight> findByRoundId(Integer roundId);
}