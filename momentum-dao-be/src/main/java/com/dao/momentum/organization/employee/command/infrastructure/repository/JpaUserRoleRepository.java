package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.UserRole;
import com.dao.momentum.organization.employee.command.domain.repository.UserRoleRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaUserRoleRepository extends UserRoleRepository, JpaRepository<UserRole, Integer> {
    @Query("SELECT u.userRoleId FROM UserRole u WHERE u.userRoleName IN :roleNames")
    List<Integer> findIdsByUserRoleNames(@Param("roleNames") List<String> roleNames);
}
