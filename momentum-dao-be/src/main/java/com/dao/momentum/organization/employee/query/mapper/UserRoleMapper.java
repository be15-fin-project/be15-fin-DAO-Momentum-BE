package com.dao.momentum.organization.employee.query.mapper;

import com.dao.momentum.organization.employee.query.dto.response.EmployeeRoleDTO;
import com.dao.momentum.organization.employee.query.dto.response.UserRoleDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    List<String> findByEmpId (long empId);

    List<UserRoleDTO> getUserRoles();

    List<EmployeeRoleDTO> getEmployeeRoles(long empId);
}
