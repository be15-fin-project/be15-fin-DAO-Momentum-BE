package com.dao.momentum.evaluation.hr.command.infrastructure.repository;

import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHrRateRepository extends JpaRepository<HrRate, Integer>, HrRateRepository {
}