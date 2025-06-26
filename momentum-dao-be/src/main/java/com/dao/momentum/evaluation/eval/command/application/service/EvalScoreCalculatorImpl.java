package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreCalculator;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalScoreCalculatorImpl implements EvalScoreCalculator {

    private final HrWeightRepository hrWeightRepository;

    @Override
    public int calculateScore(Long empId, EvalSubmitRequest request) {
        Integer formId = request.getFormId();
        List<EvalFactorScoreDto> factors = request.getFactorScores();

        if (factors == null || factors.isEmpty()) {
            throw new EvalException(ErrorCode.EVAL_INVALID_NOT_EXIST);
        }

        if (formId != null && formId == 4) {
            HrWeight weight = hrWeightRepository.findByRoundId(request.getRoundId())
                    .orElseThrow(() -> new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND));

            Map<Integer, Integer> scoreMap = factors.stream()
                    .collect(Collectors.toMap(EvalFactorScoreDto::getPropertyId, EvalFactorScoreDto::getScore));

            return (
                    scoreMap.getOrDefault(1, 0) * weight.getPerformWt() +
                    scoreMap.getOrDefault(2, 0) * weight.getTeamWt() +
                    scoreMap.getOrDefault(3, 0) * weight.getAttitudeWt() +
                    scoreMap.getOrDefault(4, 0) * weight.getGrowthWt() +
                    scoreMap.getOrDefault(5, 0) * weight.getEngagementWt() +
                    scoreMap.getOrDefault(6, 0) * weight.getResultWt()
            ) / 100;
        } else {
            return factors.stream()
                    .mapToInt(EvalFactorScoreDto::getScore)
                    .sum() / factors.size();
        }
    }

    @Override
    public int calculateOverallScore(Map<Integer, Integer> scoreMap, HrWeight weight) {
        return (
                scoreMap.getOrDefault(1, 0) * weight.getPerformWt() +
                        scoreMap.getOrDefault(2, 0) * weight.getTeamWt() +
                        scoreMap.getOrDefault(3, 0) * weight.getAttitudeWt() +
                        scoreMap.getOrDefault(4, 0) * weight.getGrowthWt() +
                        scoreMap.getOrDefault(5, 0) * weight.getEngagementWt() +
                        scoreMap.getOrDefault(6, 0) * weight.getResultWt()
        ) / 100;
    }
}
