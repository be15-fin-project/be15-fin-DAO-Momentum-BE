package com.dao.momentum.organization.company.command.infrastructure.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.Holiday;
import com.dao.momentum.organization.company.command.domain.repository.HolidayRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHolidayRepository extends HolidayRepository, JpaRepository<Holiday, Integer> {
}
