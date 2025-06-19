package com.dao.momentum.organization.employee.query.mapper;

import com.dao.momentum.organization.employee.query.dto.request.EmployeeSearchDTO;
import com.dao.momentum.organization.employee.query.dto.response.EmployeeSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminEmployeeMapper {
    List<EmployeeSummaryDTO> getEmployees(@Param("request") EmployeeSearchDTO request);

    long countEmployees(@Param("request") EmployeeSearchDTO request);
}
