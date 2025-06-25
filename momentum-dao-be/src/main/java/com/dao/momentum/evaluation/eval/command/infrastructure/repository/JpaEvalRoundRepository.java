package com.dao.momentum.evaluation.eval.command.infrastructure.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalRoundRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaEvalRoundRepository extends JpaRepository<EvalRound, Integer>, EvalRoundRepository {

    @Override
    Optional<EvalRound> findById(Integer roundId);

}