package com.dao.momentum.evaluation.eval.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.command.application.dto.request.EvalFactorScoreDto;
import com.dao.momentum.evaluation.eval.command.application.service.EvalScoreService;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EvalScoreServiceImpl implements EvalScoreService {

    private final EvalScoreRepository evalScoreRepository;

    @Override
    @Transactional
    public void save(EvalScore score) {
        evalScoreRepository.save(score);
    }

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

    @Override
    @Transactional
    public void updateScores(Long resultId, Map<Integer, Integer> scoreMap) {
        evalScoreRepository.deleteByResultId(resultId);

        for (Map.Entry<Integer, Integer> entry : scoreMap.entrySet()) {
            EvalScore score = EvalScore.builder()
                    .resultId(resultId)
                    .propertyId(entry.getKey())
                    .score(entry.getValue())
                    .build();
            evalScoreRepository.save(score);
        }
    }

    @Override
    @Transactional
    public void deleteByResultId(Long resultId) {
        evalScoreRepository.deleteByResultId(resultId);
    }

    @Override
    @Transactional
    public void saveAll(List<EvalScore> scoreEntities) {
        evalScoreRepository.saveAll(scoreEntities);
    }
}
