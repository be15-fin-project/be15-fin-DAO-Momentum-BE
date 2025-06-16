package com.dao.momentum.organization.company.command.domain.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.Holiday;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository {
    boolean existsByDate(LocalDate date);
}
