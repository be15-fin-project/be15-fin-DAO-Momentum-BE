package com.dao.momentum.organization.department.query.service;


import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.mapper.DepartmentMapper;
import com.dao.momentum.organization.employee.query.dto.response.DepartmentMemberDTO;
import com.dao.momentum.organization.employee.query.mapper.EmployeeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentQueryServiceTest {
    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private EmployeeMapper employeeMapper;

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

    @Test
    @DisplayName("부서 상세 조회_성공")
    void getDepartmentDetails_success(){
        int deptId = 1;
        DepartmentDetailDTO departmentDetailDTO = DepartmentDetailDTO.builder()
                .name("경영지원본부")
                .contact("000-0001-0001")
                .createdAt(LocalDate.parse("2025-06-13"))
                .build();

        DepartmentMemberDTO member1 = DepartmentMemberDTO.builder()
                .name("홍길동")
                .contact("010-1234-5678")
                .email("employee1@exmaple.com")
                .position("부장")
                .build();
        DepartmentMemberDTO member2 = DepartmentMemberDTO.builder()
                .name("강감찬")
                .contact("010-1234-5678")
                .email("employee2@exmaple.com")
                .position("대리")
                .build();
        DepartmentMemberDTO member3 = DepartmentMemberDTO.builder()
                .name("홍길동")
                .contact("010-1234-5678")
                .email("employee3@exmaple.com")
                .position("사원")
                .build();

        List<DepartmentMemberDTO> departmentMemberDTOList = List.of(
                member1, member2, member3
        );

        //given
        when(departmentMapper.getDepartmentDetail(deptId)).thenReturn(departmentDetailDTO);
        when(employeeMapper.getEmployeeByDeptId(deptId)).thenReturn(departmentMemberDTOList);

        //when
        DepartmentDetailResponse response = departmentQueryService.getDepartmentDetails(deptId);

        //then
        assertEquals(response.getDepartmentDetailDTO(), departmentDetailDTO);
        assertEquals(response.getDepartmentMemberDTOList(), departmentMemberDTOList);
    }

    @Test
    @DisplayName("부서 상세 조회 실패_부서 없음")
    void getDepartmentDetails_fail_department_not_found(){
        DepartmentDetailDTO departmentDetailDTO = null;
        int deptId = 1;

        when(departmentMapper.getDepartmentDetail(deptId)).thenReturn(departmentDetailDTO);

        DepartmentException exception = assertThrows(DepartmentException.class,
                () -> departmentQueryService.getDepartmentDetails(deptId));

        assertEquals(ErrorCode.DEPARTMENT_NOT_FOUND, exception.getErrorCode());
    }


}