package com.dao.momentum.evaluation.hr.command.infrastructure.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrObjectionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrObjectionJpaRepository extends JpaRepository<HrObjection, Long>, HrObjectionRepository {

    boolean existsByResultId(Long resultId);

    @Query("SELECT COUNT(r) > 0 FROM EvalResponse r WHERE r.resultId = :resultId")
    boolean existsEvaluation(@Param("resultId") Long resultId);

    Optional<HrObjection> findById(Long objectionId);
}
