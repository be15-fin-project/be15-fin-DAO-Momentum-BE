package com.dao.momentum.organization.employee.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserRoleMapper {
    List<String> findByEmpId (long empId);

}
