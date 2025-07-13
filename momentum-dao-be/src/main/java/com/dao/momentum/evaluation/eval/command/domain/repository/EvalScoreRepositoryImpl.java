package com.dao.momentum.evaluation.eval.command.domain.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.infrastructure.repository.JpaEvalScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<EvalScore> findByResultIdAndPropertyId(Long resultId, Integer propertyId) {
        return jpaRepository.findByResultIdAndPropertyId(resultId, propertyId);
    }

    @Override
    public List<EvalScore> saveAll(List<EvalScore> scores) {
        return jpaRepository.saveAll(scores);
    }
}
