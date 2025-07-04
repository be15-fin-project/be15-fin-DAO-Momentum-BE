package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
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
        validateRateSum(dto.getRateS(), dto.getRateA(), dto.getRateB(), dto.getRateC(), dto.getRateD());

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

    @Override
    @Transactional
    public void update(Integer roundId, HrRateUpdateDTO dto) {
        validateRateSum(dto.getRateS(), dto.getRateA(), dto.getRateB(), dto.getRateC(), dto.getRateD());

        HrRate rate = hrRateRepository.findByRoundId(roundId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_RATE_NOT_FOUND));

        rate.update(dto.getRateS(), dto.getRateA(), dto.getRateB(),
                dto.getRateC(), dto.getRateD());
    }

    private void validateRateSum(int s, int a, int b, int c, int d) {
        int total = s + a + b + c + d;
        if (total != 100) {
            throw new HrException(ErrorCode.HR_RATE_INVALID_SUM);
        }
    }

    @Override
    @Transactional
    public void deleteByRoundId(int roundId) {
        hrRateRepository.deleteByRoundId(roundId);
    }

}
