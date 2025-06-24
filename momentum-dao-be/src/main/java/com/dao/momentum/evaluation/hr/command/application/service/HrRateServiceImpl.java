package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HrRateServiceImpl implements HrRateService {

    private final HrRateRepository hrRateRepository;

    @Override
    @Transactional
    public void create(int roundId, HrRateCreateDTO dto) {
        HrRate rate = HrRate.builder()
                .roundId(roundId)
                .rateS(dto.getRateS())
                .rateA(dto.getRateA())
                .rateB(dto.getRateB())
                .rateC(dto.getRateC())
                .rateD(dto.getRateD())
                .build();

        hrRateRepository.save(rate);
    }
}
