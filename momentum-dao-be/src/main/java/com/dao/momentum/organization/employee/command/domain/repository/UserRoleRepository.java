package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleRepository {
    List<Integer> findIdsByUserRoleNames(List<String> roleNames);
}
