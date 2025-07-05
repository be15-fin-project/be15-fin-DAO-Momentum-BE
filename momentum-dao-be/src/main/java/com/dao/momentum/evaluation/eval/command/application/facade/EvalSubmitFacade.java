package com.dao.momentum.evaluation.eval.command.application.facade;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalSubmitResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalResponseService;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreService;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreCalculator;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalSubmitFacade {

    private final EvalScoreCalculator scoreCalculator;
    private final EvalResponseService evalResponseService;
    private final EvalScoreService evalScoreService;

    @Transactional
    public EvalSubmitResponse submit(Long empId, EvalSubmitRequest request) {
        log.info("[EvalSubmitFacade] submit() 호출 시작 - evaluatorEmpId={}, formId={}", empId, request.getFormId());

        try {
            // 평가 점수 계산
            int finalScore = scoreCalculator.calculateScore(empId, request);
            log.info("계산된 최종 점수 - evaluatorEmpId={}, finalScore={}", empId, finalScore);

            // 평가 결과 저장
            EvalResponse response = evalResponseService.saveResponse(empId, request, finalScore);
            log.info("저장된 평가 결과 - resultId={}, evaluatorEmpId={}", response.getResultId(), empId);

            // 평가 항목 점수 저장
            evalScoreService.saveFactorScores(response.getResultId(), request.getFactorScores());
            log.info("평가 항목 점수 저장 완료 - resultId={}, factorScoresCount={}", response.getResultId(), request.getFactorScores().size());

            log.info("평가 제출 완료 - resultId={}, finalScore={}", response.getResultId(), finalScore);

            return EvalSubmitResponse.builder()
                    .resultId(response.getResultId())
                    .message("평가가 성공적으로 제출되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("[EvalSubmitFacade] 평가 제출 실패 - evaluatorEmpId={}, formId={}, 에러={}", empId, request.getFormId(), e.getMessage());
            throw new EvalException(ErrorCode.EVAL_SUBMIT_FAILED);
        }
    }
}
