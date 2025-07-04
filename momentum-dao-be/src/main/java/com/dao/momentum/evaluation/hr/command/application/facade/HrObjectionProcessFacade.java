package com.dao.momentum.evaluation.hr.command.application.facade;

import com.dao.momentum.evaluation.eval.command.application.service.EvalResponseService;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreCalculator;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreService;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionProcessRequestDto;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionProcessRequestDto.EvalFactorScoreDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionProcessResponseDto;
import com.dao.momentum.evaluation.hr.command.application.service.HrObjectionService;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrObjectionProcessFacade {

    private final HrObjectionService hrObjectionService;
    private final HrWeightRepository hrWeightRepository;
    private final EvalScoreService evalScoreService;
    private final EvalScoreCalculator evalScoreCalculator;
    private final EvalResponseService evalResponseService;

    @Transactional
    public HrObjectionProcessResponseDto processObjection(Long empId, HrObjectionProcessRequestDto request) {
        Long objectionId = request.getObjectionId();

        // Step 0. objectionId → resultId
        Long resultId = hrObjectionService.getResultIdByObjectionId(objectionId);

        // Step 0-1. 평가자 본인인지 검증
        Long evaluatorEmpId = evalResponseService.getEvaluatorEmpIdByResultId(resultId); // ← 신규 메서드 필요
        if (!evaluatorEmpId.equals(empId)) {
            throw new HrException(ErrorCode.HR_EVALUATION_FORBIDDEN); // 또는 적절한 오류 코드
        }

        if (Boolean.FALSE.equals(request.getApproved())) {
            // 반려 처리
            hrObjectionService.reject(objectionId, request.getRejectReason());
            return HrObjectionProcessResponseDto.builder()
                    .objectionId(objectionId)
                    .result("반려")
                    .processedAt(java.time.LocalDateTime.now().toString())
                    .build();
        }

        // Step 2: resultId → roundId
        Integer roundId = evalResponseService.getRoundIdByResultId(resultId);

        // Step 3: 요인별 점수 저장
        Map<Integer, Integer> scoreMap = request.getFactorScores().stream()
                .collect(Collectors.toMap(EvalFactorScoreDto::getPropertyId, EvalFactorScoreDto::getScore));

        evalScoreService.deleteByResultId(resultId);
        scoreMap.forEach((propertyId, score) ->
                evalScoreService.save(EvalFactorScoreDto.toEntity(resultId, propertyId, score))
        );

        // Step 4: 가중치 기반 종합 점수 계산
        HrWeight weight = hrWeightRepository.findByRoundId(roundId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND));

        int finalScore = evalScoreCalculator.calculateOverallScore(scoreMap, weight);

        // Step 5: 평가 결과 반영
        evalResponseService.updateFinalScoreAndReason(resultId, finalScore, request.getRejectReason());

        // Step 6: 이의제기 승인 처리
        hrObjectionService.approve(objectionId, request.getRejectReason());

        return HrObjectionProcessResponseDto.builder()
                .objectionId(objectionId)
                .result("승인")
                .processedAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}
