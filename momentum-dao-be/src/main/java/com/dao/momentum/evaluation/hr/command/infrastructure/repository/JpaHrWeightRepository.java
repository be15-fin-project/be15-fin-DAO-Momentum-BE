package com.dao.momentum.evaluation.hr.command.infrastructure.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHrWeightRepository extends JpaRepository<HrWeight, Integer>, HrWeightRepository {
}