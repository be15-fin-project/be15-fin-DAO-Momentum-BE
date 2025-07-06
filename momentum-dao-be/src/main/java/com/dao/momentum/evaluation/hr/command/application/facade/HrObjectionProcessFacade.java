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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
        Long objectionId = request.objectionId();
        log.info("[HrObjectionProcessFacade] processObjection() 호출 시작 - empId={}, objectionId={}", empId, objectionId);

        // Step 0. objectionId → resultId
        Long resultId = hrObjectionService.getResultIdByObjectionId(objectionId);
        log.info("objectionId={}에 해당하는 resultId={}", objectionId, resultId);

        // Step 0-1. 평가자 본인인지 검증
        Long evaluatorEmpId = evalResponseService.getEvaluatorEmpIdByResultId(resultId); // ← 신규 메서드 필요
        if (!evaluatorEmpId.equals(empId)) {
            log.error("평가자 본인 인증 실패 - empId={}, evaluatorEmpId={}", empId, evaluatorEmpId);
            throw new HrException(ErrorCode.HR_EVALUATION_FORBIDDEN);
        }
        log.info("평가자 본인 인증 완료 - empId={}, evaluatorEmpId={}", empId, evaluatorEmpId);

        if (Boolean.FALSE.equals(request.approved())) {
            // 반려 처리
            log.info("이의제기 반려 처리 - objectionId={}, rejectReason={}", objectionId, request.rejectReason());
            hrObjectionService.reject(objectionId, request.rejectReason());
            return HrObjectionProcessResponseDto.builder()
                    .objectionId(objectionId)
                    .result("반려")
                    .processedAt(java.time.LocalDateTime.now().toString())
                    .build();
        }

        // Step 2: resultId → roundId
        Integer roundId = evalResponseService.getRoundIdByResultId(resultId);
        log.info("resultId={}에 해당하는 roundId={}", resultId, roundId);

        // Step 3: 요인별 점수 저장
        Map<Integer, Integer> scoreMap = request.factorScores().stream()
                .collect(Collectors.toMap(EvalFactorScoreDto::propertyId, EvalFactorScoreDto::score));

        log.info("요인별 점수 저장 시작 - resultId={}, scoreMap={}", resultId, scoreMap);

        evalScoreService.deleteByResultId(resultId);
        log.info("기존 점수 삭제 완료 - resultId={}", resultId);

        scoreMap.forEach((propertyId, score) -> {
            evalScoreService.save(EvalFactorScoreDto.toEntity(resultId, propertyId, score));
            log.info("점수 저장 완료 - resultId={}, propertyId={}, score={}", resultId, propertyId, score);
        });

        // Step 4: 가중치 기반 종합 점수 계산
        HrWeight weight = hrWeightRepository.findByRoundId(roundId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND));
        log.info("가중치 조회 완료 - roundId={}, weight={}", roundId, weight);

        int finalScore = evalScoreCalculator.calculateOverallScore(scoreMap, weight);
        log.info("가중치 기반 종합 점수 계산 완료 - finalScore={}", finalScore);

        // Step 5: 평가 결과 반영
        evalResponseService.updateFinalScoreAndReason(resultId, finalScore, request.rejectReason());
        log.info("평가 결과 반영 완료 - resultId={}, finalScore={}", resultId, finalScore);

        // Step 6: 이의제기 승인 처리
        hrObjectionService.approve(objectionId, request.rejectReason());
        log.info("이의제기 승인 처리 완료 - objectionId={}", objectionId);

        return HrObjectionProcessResponseDto.builder()
                .objectionId(objectionId)
                .result("승인")
                .processedAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}
