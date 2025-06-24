package com.dao.momentum.evaluation.hr.command.domain.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;

public interface HrRateRepository {
    HrRate save(HrRate hrRate);
}