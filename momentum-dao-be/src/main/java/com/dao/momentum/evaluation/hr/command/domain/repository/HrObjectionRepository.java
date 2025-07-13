package com.dao.momentum.evaluation.hr.command.domain.repository;

import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HrObjectionRepository {

    boolean existsByResultId(Long resultId);

    HrObjection save(HrObjection objection);

    boolean existsEvaluation(Long resultId);   // 평가 결과 존재 여부

    Optional<HrObjection> findById(Long objectionId);

    Optional<Long> findResultIdByObjectionId(@Param("objectionId") Long objectionId);

    boolean existsByResultIdAndIsDeleted(Long resultId, UseStatus isDeleted);
}
