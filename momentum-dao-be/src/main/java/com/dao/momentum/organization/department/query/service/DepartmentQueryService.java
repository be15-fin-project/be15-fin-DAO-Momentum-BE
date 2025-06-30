package com.dao.momentum.organization.department.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.department.query.dto.response.*;
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
        if(departmentDetailDTO == null){
            throw new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        List<DepartmentMemberDTO> departmentMemberDTOList = employeeMapper.getEmployeeByDeptId(deptId);


        return DepartmentDetailResponse.builder()
                .departmentDetailDTO(departmentDetailDTO)
                .departmentMemberDTOList(departmentMemberDTOList)
                .build();
    }

    @Transactional(readOnly = true)
    public DepartmentTreeResponse getDepartmentTreeWithEmployees() {
        List<DepartmentNodeDTO> flatList = departmentMapper.getDepartmentListWithEmployees();

        Map<Integer, DepartmentNodeDTO> deptMap = new HashMap<>();
        List<DepartmentNodeDTO> rootList = new ArrayList<>();

        // 모든 부서를 Map에 저장
        for (DepartmentNodeDTO dept : flatList) {
            deptMap.put(dept.getDeptId(), dept);
        }

        // 트리 구조 구성
        for (DepartmentNodeDTO dept : flatList) {
            Integer parentId = dept.getParentDeptId();
            if (parentId == null) {
                rootList.add(dept);
            } else {
                DepartmentNodeDTO parent = deptMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(dept);
                }
            }
        }

        return DepartmentTreeResponse.builder()
                .departmentNodeDTOList(rootList)
                .build();
    }

    @Transactional(readOnly = true)
    public LeafDepartmentResponse getLeafDepartments() {
        List<LeafDepartmentDTO> dtoList = departmentMapper.getLeafDepartment();


        return LeafDepartmentResponse.builder()
                .leafDepartmentDTOList(dtoList)
                .build();
    }
}
