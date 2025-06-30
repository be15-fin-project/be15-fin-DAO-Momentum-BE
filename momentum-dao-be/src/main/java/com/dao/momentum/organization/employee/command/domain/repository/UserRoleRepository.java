package com.dao.momentum.organization.employee.command.domain.repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository {
    List<Integer> findIdsByUserRoleNames(List<String> roleNames);

    @Query("""
    SELECT r.userRoleId FROM UserRole r
""")
    List<Integer> findAllIds();

}
