package com.dao.momentum.organization.department.query.service;


import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.mapper.DepartmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentQueryServiceTest {
    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentQueryService departmentQueryService;


    @Test
    @DisplayName("[Service] 부서 정보 조회_성공")
    void getDepartmentsInfo_success(){
        // given
        DepartmentInfoDTO root = DepartmentInfoDTO.builder()
                .deptId(1)
                .name("본사")
                .parentDeptId(null)
                .build();

        DepartmentInfoDTO child1 = DepartmentInfoDTO.builder()
                .deptId(2)
                .name("인사팀")
                .parentDeptId(1)
                .build();

        DepartmentInfoDTO child2 = DepartmentInfoDTO.builder()
                .deptId(3)
                .name("개발팀")
                .parentDeptId(1)
                .build();

        DepartmentInfoDTO subChild = DepartmentInfoDTO.builder()
                .deptId(4)
                .name("백엔드팀")
                .parentDeptId(3)
                .build();

        List<DepartmentInfoDTO> mockList = List.of(root, child1, child2, subChild);

        when(departmentMapper.getDepartments()).thenReturn(mockList);

        // when
        DepartmentsInfoResponse response = departmentQueryService.getDepartmentsInfo();

        // then
        List<DepartmentInfoDTO> result = response.getDepartmentInfoDTOList();
        assertThat(result).hasSize(1); // root만 최상위

        DepartmentInfoDTO rootNode = result.get(0);
        assertThat(rootNode.getDeptId()).isEqualTo(1);
        assertThat(rootNode.getChildDept()).hasSize(2); // 인사팀, 개발팀

        DepartmentInfoDTO devTeam = rootNode.getChildDept().stream()
                .filter(d -> d.getName().equals("개발팀"))
                .findFirst().orElseThrow();

        assertThat(devTeam.getChildDept()).hasSize(1);
        assertThat(devTeam.getChildDept().get(0).getName()).isEqualTo("백엔드팀");
    }
}