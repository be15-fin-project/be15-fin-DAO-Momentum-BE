package com.dao.momentum.organization.employee.query.mapper;

import com.dao.momentum.organization.employee.query.dto.response.DepartmentMemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<DepartmentMemberDTO> getEmployeeByDeptId(int deptId);
}
