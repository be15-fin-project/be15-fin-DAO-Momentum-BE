package com.dao.momentum.organization.department.query.mapper;

import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentNodeDTO;
import com.dao.momentum.organization.department.query.dto.response.LeafDepartmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<DepartmentInfoDTO> getDepartments();

    DepartmentDetailDTO getDepartmentDetail(int deptId);

    List<DepartmentNodeDTO> getDepartmentListWithEmployees();

    List<LeafDepartmentDTO> getLeafDepartment();
}
