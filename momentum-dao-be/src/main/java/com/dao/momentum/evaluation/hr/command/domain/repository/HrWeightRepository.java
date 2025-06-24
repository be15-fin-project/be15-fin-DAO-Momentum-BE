package com.dao.momentum.evaluation.hr.command.domain.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;

public interface HrWeightRepository {
    HrWeight save(HrWeight hrWeight);
}