package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;

public interface HrRateService {
    void create(int roundId, HrRateCreateDTO dto);
    void update(Integer roundId, HrRateUpdateDTO dto);

    void deleteByRoundId(int roundId);
}
