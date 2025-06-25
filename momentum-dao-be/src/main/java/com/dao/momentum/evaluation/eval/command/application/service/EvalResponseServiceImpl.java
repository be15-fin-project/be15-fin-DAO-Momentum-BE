package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalSubmitRequest;
import com.dao.momentum.evaluation.eval.command.application.service.EvalResponseService;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EvalResponseServiceImpl implements EvalResponseService {

    private final EvalResponseRepository evalResponseRepository;

    @Override
    public EvalResponse saveResponse(Long empId, EvalSubmitRequest request, int finalScore) {

        // 중복 체크
        boolean exists;
        if (request.getTargetId() == null) {
            exists = evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetIdIsNull(
                    request.getRoundId(), request.getFormId(), empId);
        } else {
            exists = evalResponseRepository.existsByRoundIdAndFormIdAndEvalIdAndTargetId(
                    request.getRoundId(), request.getFormId(), empId, request.getTargetId());
        }

        if (exists) {
            throw new EvalException(ErrorCode.EVAL_ALREADY_SUBMITTED);
        }

        EvalResponse response = EvalResponse.builder()
                .roundId(request.getRoundId())
                .formId(request.getFormId())
                .evalId(empId)
                .targetId(request.getTargetId())
                .score(finalScore)
                .reason(request.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        return evalResponseRepository.save(response);
    }

    @Override
    @Transactional
    public void updateFinalScoreAndReason(Long resultId, int score, String reason) {
        EvalResponse response = evalResponseRepository.findById(resultId)
                .orElseThrow(() -> new EvalException(ErrorCode.EVALUATION_NOT_FOUND));

        response.updateScoreAndReason(score, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getRoundIdByResultId(Long resultId) {
        return evalResponseRepository.findRoundIdByResultId(resultId)
                .orElseThrow(() -> new EvalException(ErrorCode.EVALUATION_NOT_FOUND));
    }

}
