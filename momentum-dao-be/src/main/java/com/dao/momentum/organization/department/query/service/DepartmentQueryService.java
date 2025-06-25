package com.dao.momentum.organization.department.query.service;

import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.mapper.DepartmentMapper;
import com.dao.momentum.organization.employee.query.dto.response.DepartmentMemberDTO;
import com.dao.momentum.organization.employee.query.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentQueryService {
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public DepartmentDetailResponse getDepartmentDetails(int deptId) {
        DepartmentDetailDTO departmentDetailDTO =  departmentMapper.getDepartmentDetail(deptId);
        List<DepartmentMemberDTO> departmentMemberDTOList = employeeMapper.getEmployeeByDeptId(deptId);


        return DepartmentDetailResponse.builder()
                .departmentDetailDTO(departmentDetailDTO)
                .departmentMemberDTOList(departmentMemberDTOList)
                .build();
    }
}
