package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;

public interface HrWeightService {
    void create(int roundId, HrWeightCreateDTO dto);
}
