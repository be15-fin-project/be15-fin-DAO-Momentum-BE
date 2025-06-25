package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreService;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvalScoreServiceImpl implements EvalScoreService {

    private final EvalScoreRepository evalScoreRepository;

    @Override
    public void saveFactorScores(Long resultId, List<EvalFactorScoreDto> factorScores) {
        if (resultId == null) {
            throw new EvalException(ErrorCode.INVALID_RESULT_REQUEST);
        }

        if (factorScores == null || factorScores.isEmpty()) {
            throw new EvalException(ErrorCode.EVAL_INVALID_NOT_EXIST);
        }

        List<EvalScore> scores = factorScores.stream()
                .map(dto -> EvalScore.builder()
                        .resultId(resultId)
                        .propertyId(dto.getPropertyId())
                        .score(dto.getScore())
                        .build())
                .toList();

        evalScoreRepository.saveAll(scores);
    }
}
