package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalScoreCalculatorImpl implements EvalScoreCalculator {

    private final HrWeightRepository hrWeightRepository;

    @Override
    public int calculateScore(Long empId, EvalSubmitRequest request) {
        log.info("[EvalScoreCalculatorImpl] calculateScore() 호출 시작 - empId={}, formId={}, roundId={}",
                empId, request.formId(), request.roundId());

        Integer formId = request.formId();
        List<EvalFactorScoreDto> factors = request.factorScores();

        if (factors == null || factors.isEmpty()) {
            log.error("유효하지 않은 평가 항목 - empId={}, formId={}", empId, formId);
            throw new EvalException(ErrorCode.EVAL_INVALID_NOT_EXIST);
        }

        int result;

        try {
            if (formId != null && formId == 4) {
                HrWeight weight = hrWeightRepository.findByRoundId(request.roundId())
                        .orElseThrow(() -> {
                            log.error("HR 가중치 정보를 찾을 수 없습니다 - roundId={}", request.roundId());
                            return new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND);
                        });

                Map<Integer, Integer> scoreMap = factors.stream()
                        .collect(Collectors.toMap(EvalFactorScoreDto::propertyId, EvalFactorScoreDto::score));

                result = (
                        scoreMap.getOrDefault(1, 0) * weight.getPerformWt() +
                                scoreMap.getOrDefault(2, 0) * weight.getTeamWt() +
                                scoreMap.getOrDefault(3, 0) * weight.getAttitudeWt() +
                                scoreMap.getOrDefault(4, 0) * weight.getGrowthWt() +
                                scoreMap.getOrDefault(5, 0) * weight.getEngagementWt() +
                                scoreMap.getOrDefault(6, 0) * weight.getResultWt()
                ) / 100;

            } else {
                result = factors.stream()
                        .mapToInt(EvalFactorScoreDto::score)
                        .sum() / factors.size();
            }

            log.info("평가 점수 계산 완료 - empId={}, finalScore={}", empId, result);

        } catch (Exception e) {
            log.error("평가 점수 계산 중 오류 발생 - empId={}, formId={}, roundId={}, 에러={}", empId, formId, request.roundId(), e.getMessage());
            throw new EvalException(ErrorCode.EVAL_SCORE_CALCULATION_FAILED);
        }

        return result;
    }

    @Override
    public int calculateOverallScore(Map<Integer, Integer> scoreMap, HrWeight weight) {
        log.info("[EvalScoreCalculatorImpl] calculateOverallScore() 호출 시작");

        int result = (
                scoreMap.getOrDefault(1, 0) * weight.getPerformWt() +
                        scoreMap.getOrDefault(2, 0) * weight.getTeamWt() +
                        scoreMap.getOrDefault(3, 0) * weight.getAttitudeWt() +
                        scoreMap.getOrDefault(4, 0) * weight.getGrowthWt() +
                        scoreMap.getOrDefault(5, 0) * weight.getEngagementWt() +
                        scoreMap.getOrDefault(6, 0) * weight.getResultWt()
        ) / 100;

        log.info("최종 종합 점수 계산 완료 - score={}", result);
        return result;
    }
}
