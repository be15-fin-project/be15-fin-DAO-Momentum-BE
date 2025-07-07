package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalResponseServiceImpl implements EvalResponseService {

    private final EvalResponseRepository evalResponseRepository;

    @Override
    public EvalResponse saveResponse(Long empId, EvalSubmitRequest request, int finalScore) {
        log.info("[EvalResponseServiceImpl] saveResponse() 호출 - empId={}, formId={}, targetId={}", empId, request.formId(), request.targetId());

        boolean exists;
        if (request.targetId() == null) {
            exists = evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetIdIsNull(
                    request.roundId(), request.formId(), empId);
        } else {
            exists = evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetId(
                    request.roundId(), request.formId(), empId, request.targetId());
        }

        if (exists) {
            log.warn("이미 제출된 평가입니다 - empId={}, formId={}, targetId={}", empId, request.formId(), request.targetId());
            throw new EvalException(ErrorCode.EVAL_ALREADY_SUBMITTED);
        }

        EvalResponse response = EvalResponse.builder()
                .roundId(request.roundId())
                .formId(request.formId())
                .evalId(empId)
                .targetId(request.targetId())
                .score(finalScore)
                .reason(request.reason())
                .createdAt(LocalDateTime.now())
                .build();

        EvalResponse saved = evalResponseRepository.save(response);
        log.info("평가 결과 저장 완료 - resultId={}, roundId={}, formId={}, score={}", saved.getResultId(), saved.getRoundId(), saved.getFormId(), saved.getScore());
        return saved;
    }

    @Override
    @Transactional
    public void updateFinalScoreAndReason(Long resultId, int score, String reason) {
        log.info("[EvalResponseServiceImpl] updateFinalScoreAndReason() 호출 - resultId={}, newScore={}, newReason={}", resultId, score, reason);

        EvalResponse response = evalResponseRepository.findById(resultId)
                .orElseThrow(() -> {
                    log.error("평가 결과를 찾을 수 없습니다 - resultId={}", resultId);
                    return new EvalException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        response.updateScoreAndReason(score, reason);

        log.info("최종 점수 및 사유 수정 완료 - resultId={}, updatedScore={}, updatedReason={}", resultId, score, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getRoundIdByResultId(Long resultId) {
        log.info("[EvalResponseServiceImpl] getRoundIdByResultId() 호출 - resultId={}", resultId);

        Integer roundId = evalResponseRepository.findRoundIdByResultId(resultId)
                .orElseThrow(() -> {
                    log.error("평가 결과를 찾을 수 없습니다 - resultId={}", resultId);
                    return new EvalException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        log.info("조회된 roundId={}", roundId);
        return roundId;
    }

    @Override
    public Long getEvaluatorEmpIdByResultId(Long resultId) {
        log.info("[EvalResponseServiceImpl] getEvaluatorEmpIdByResultId() 호출 - resultId={}", resultId);

        Long empId = evalResponseRepository.findEvalIdByResultId(resultId)
                .orElseThrow(() -> {
                    log.error("평가자를 찾을 수 없습니다 - resultId={}", resultId);
                    return new EvalException(ErrorCode.EVAL_RESULT_NOT_FOUND);
                });

        log.info("조회된 evaluatorEmpId={}", empId);
        return empId;
    }
}
