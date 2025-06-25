package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;

import java.time.LocalDate;
import java.util.List;

public interface AppointRepository {

    Appoint save(Appoint appoint);

    List<Appoint> findByAppointDateLessThanEqual(LocalDate today);
}
