package com.dao.momentum.organization.department.query.mapper;

import com.dao.momentum.organization.department.query.dto.response.DepartmentFlatDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<DepartmentInfoDTO> getDepartments();
}
