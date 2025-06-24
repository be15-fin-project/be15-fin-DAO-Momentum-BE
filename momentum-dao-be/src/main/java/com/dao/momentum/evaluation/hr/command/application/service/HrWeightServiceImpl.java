package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
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
}
