package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;
import com.dao.momentum.organization.employee.command.domain.repository.AppointRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAppointRepository extends AppointRepository, JpaRepository<Appoint, Long> {
}
