package com.dao.momentum.evaluation.eval.command.infrastructure.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaEvalResponseRepository extends JpaRepository<EvalResponse, Long>, EvalResponseRepository {

    @Query("SELECT e.roundId FROM EvalResponse e WHERE e.resultId = :resultId")
    Optional<Integer> findRoundIdByResultId(@Param("resultId") Long resultId);

}