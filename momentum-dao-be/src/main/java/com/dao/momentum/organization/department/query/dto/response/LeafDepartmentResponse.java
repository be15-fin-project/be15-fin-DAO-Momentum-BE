package com.dao.momentum.organization.department.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeafDepartmentResponse {
    private List<LeafDepartmentDTO> leafDepartmentDTOList;
}
