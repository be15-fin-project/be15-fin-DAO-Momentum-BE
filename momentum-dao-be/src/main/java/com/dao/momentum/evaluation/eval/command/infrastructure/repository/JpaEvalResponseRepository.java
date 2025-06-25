package com.dao.momentum.evaluation.eval.command.infrastructure.repository;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalResponse;
import com.dao.momentum.evaluation.eval.command.domain.repository.EvalResponseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEvalResponseRepository extends JpaRepository<EvalResponse, Long>, EvalResponseRepository {
}