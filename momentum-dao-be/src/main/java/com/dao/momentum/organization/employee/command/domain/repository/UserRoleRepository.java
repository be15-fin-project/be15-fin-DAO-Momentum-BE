package com.dao.momentum.organization.employee.command.domain.repository;

import java.util.List;

public interface UserRoleRepository {
    List<Integer> findIdsByUserRoleNames(List<String> roleNames);
}
