package com.dao.momentum.evaluation.eval.command.infrastructure.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalScoreRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaEvalScoreRepository extends JpaRepository<EvalScore, Long> {

    void deleteByResultId(Long resultId);
}