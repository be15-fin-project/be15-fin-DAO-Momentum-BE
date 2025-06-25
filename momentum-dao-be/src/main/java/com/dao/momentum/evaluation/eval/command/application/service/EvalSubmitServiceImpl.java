package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalSubmitResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EvalSubmitServiceImpl implements EvalSubmitService {

    private final EvalResponseRepository evalResponseRepository;
    private final EvalScoreRepository evalScoreRepository;
    private final HrWeightRepository hrWeightRepository;

    @Override
    @Transactional
    public EvalSubmitResponse submitEvaluation(Long empId, EvalSubmitRequest request) {
        int finalScore;
        if (request.getFormId() != null && request.getFormId() == 4) {
            // [1] formId == 4 → 인사 평가 가중치 계산
            HrWeight weight = hrWeightRepository.findByRoundId(request.getRoundId())
                    .orElseThrow(() -> new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND));

            Map<Integer, Integer> scoreMap = request.getFactorScores().stream()
                    .collect(Collectors.toMap(EvalFactorScoreDto::getPropertyId, EvalFactorScoreDto::getScore));

            finalScore = (
                    scoreMap.getOrDefault(1, 0) * weight.getPerformWt() +
                            scoreMap.getOrDefault(2, 0) * weight.getTeamWt() +
                            scoreMap.getOrDefault(3, 0) * weight.getAttitudeWt() +
                            scoreMap.getOrDefault(4, 0) * weight.getGrowthWt() +
                            scoreMap.getOrDefault(5, 0) * weight.getEngagementWt() +
                            scoreMap.getOrDefault(6, 0) * weight.getResultWt()
            ) / 100;

        } else {
            // [2] formId != 4 → 요인별 점수 평균
            List<EvalFactorScoreDto> factorScores = request.getFactorScores();
            finalScore = factorScores.stream()
                    .mapToInt(EvalFactorScoreDto::getScore)
                    .sum() / factorScores.size();
        }
        // 1. 평가 결과 저장
        EvalResponse response = EvalResponse.builder()
                .roundId(request.getRoundId())
                .formId(request.getFormId())
                .evalId(empId)
                .targetId(request.getTargetId())
                .score(finalScore)
                .reason(request.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        evalResponseRepository.save(response);

        // 2. 요인별 점수 저장
        List<EvalScore> scores = request.getFactorScores().stream()
                .map(dto -> EvalScore.builder()
                        .resultId(response.getResultId())
                        .propertyId(dto.getPropertyId())
                        .score(dto.getScore())
                        .build())
                .toList();

        evalScoreRepository.saveAll(scores);

        return EvalSubmitResponse.builder()
                .resultId(response.getResultId())
                .message("평가가 성공적으로 제출되었습니다.")
                .build();
    }

}
