package com.dao.momentum.evaluation.eval.command.application.facade;

import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.dto.response.EvalSubmitResponse;
import com.dao.momentum.evaluation.eval.command.application.service.EvalResponseService;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreService;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreCalculator;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvalSubmitFacade {

    private final EvalScoreCalculator scoreCalculator;
    private final EvalResponseService evalResponseService;
    private final EvalScoreService evalScoreService;

    @Transactional
    public EvalSubmitResponse submit(Long empId, EvalSubmitRequest request) {
        // 1. 종합 점수 계산
        int finalScore = scoreCalculator.calculateScore(empId, request);

        // 2. 평가 결과 저장
        EvalResponse response = evalResponseService.saveResponse(empId, request, finalScore);

        // 3. 요인별 점수 저장
        evalScoreService.saveFactorScores(response.getResultId(), request.getFactorScores());

        // 4. 결과 반환
        return EvalSubmitResponse.builder()
                .resultId(response.getResultId())
                .message("평가가 성공적으로 제출되었습니다.")
                .build();
    }
}
