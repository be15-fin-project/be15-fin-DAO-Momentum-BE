package com.dao.momentum.organization.department.query.dto.response;

import com.dao.momentum.organization.employee.query.dto.response.DepartmentMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class DepartmentDetailResponse {
    private DepartmentDetailDTO departmentDetailDTO;
    private List<DepartmentMemberDTO> departmentMemberDTOList;
}
