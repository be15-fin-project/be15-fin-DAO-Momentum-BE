package com.dao.momentum.organization.department.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DepartmentsInfoResponse {
    private List<DepartmentInfoDTO> departmentInfoDTOList;
}
