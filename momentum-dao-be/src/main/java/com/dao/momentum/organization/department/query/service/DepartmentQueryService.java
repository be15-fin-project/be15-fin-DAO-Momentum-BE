package com.dao.momentum.organization.department.query.service;


import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentQueryService {
    private final DepartmentMapper departmentMapper;
    public DepartmentsInfoResponse getDepartmentsInfo() {
        List<DepartmentInfoDTO> allDepts = departmentMapper.getDepartments();
        Map<Integer, DepartmentInfoDTO> deptMap = new HashMap<>();
        List<DepartmentInfoDTO> result = new ArrayList<>();

        //Map에 모두 삽입
        for (DepartmentInfoDTO dept : allDepts) {
            deptMap.put(dept.getDeptId(), dept);
        }

        for (DepartmentInfoDTO InfoDTO : allDepts) {
            //null 이면 루트 노드
            if (InfoDTO.getParentDeptId() == null) {
                result.add(InfoDTO);
            }
            else {
                DepartmentInfoDTO parent = deptMap.get(InfoDTO.getParentDeptId());
                if (parent != null) {
                    if (parent.getChildDept() == null) {
                        parent.setChildDept(new ArrayList<>());
                    }
                    parent.getChildDept().add(InfoDTO);
                }
            }
        }
        return DepartmentsInfoResponse.builder()
                .departmentInfoDTOList(result)
                .build();
    }
}
