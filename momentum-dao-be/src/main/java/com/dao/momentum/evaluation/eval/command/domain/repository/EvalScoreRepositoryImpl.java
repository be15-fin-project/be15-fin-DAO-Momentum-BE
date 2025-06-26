package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.infrastructure.repository.JpaEvalScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EvalScoreRepositoryImpl implements EvalScoreRepository {

    private final JpaEvalScoreRepository jpaRepository;

    @Override
    public void deleteByResultId(Long resultId) {
        jpaRepository.deleteByResultId(resultId);
    }

    @Override
    public EvalScore save(EvalScore score) {
        return jpaRepository.save(score);
    }

    @Override
    public List<EvalScore> saveAll(List<EvalScore> scores) {
        return jpaRepository.saveAll(scores);
    }
}
