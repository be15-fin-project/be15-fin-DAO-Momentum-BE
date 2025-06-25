package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HrWeightServiceImpl implements HrWeightService {

    private final HrWeightRepository hrWeightRepository;

    @Override
    @Transactional
    public void create(int roundId, HrWeightCreateDTO dto) {
        validateWeightSum(dto.getPerformWt(), dto.getTeamWt(), dto.getAttitudeWt(),
                dto.getGrowthWt(), dto.getEngagementWt(), dto.getResultWt());

        HrWeight weight = HrWeight.builder()
                .roundId(roundId)
                .performWt(dto.getPerformWt())
                .teamWt(dto.getTeamWt())
                .attitudeWt(dto.getAttitudeWt())
                .growthWt(dto.getGrowthWt())
                .engagementWt(dto.getEngagementWt())
                .resultWt(dto.getResultWt())
                .build();

        hrWeightRepository.save(weight);
    }

    @Override
    @Transactional
    public void update(Integer roundId, HrWeightUpdateDTO dto) {
        validateWeightSum(dto.getPerformWt(), dto.getTeamWt(), dto.getAttitudeWt(),
                dto.getGrowthWt(), dto.getEngagementWt(), dto.getResultWt());

        HrWeight weight = hrWeightRepository.findByRoundId(roundId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND));

        weight.update(dto.getPerformWt(), dto.getTeamWt(), dto.getAttitudeWt(),
                dto.getGrowthWt(), dto.getEngagementWt(), dto.getResultWt());
    }

    private void validateWeightSum(int perform, int team, int attitude,
                                   int growth, int engagement, int result) {
        int total = perform + team + attitude + growth + engagement + result;
        if (total != 100) {
            throw new HrException(ErrorCode.HR_WEIGHT_INVALID_SUM);
        }
    }

}
