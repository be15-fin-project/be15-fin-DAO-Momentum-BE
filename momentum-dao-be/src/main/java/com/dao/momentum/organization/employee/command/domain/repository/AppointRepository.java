package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;

public interface AppointRepository {

    Appoint save(Appoint appoint);
}
