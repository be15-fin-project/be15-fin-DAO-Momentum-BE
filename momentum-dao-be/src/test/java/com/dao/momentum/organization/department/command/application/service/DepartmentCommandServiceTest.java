package com.dao.momentum.organization.department.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentCommandServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DepartmentCommandService departmentCommandService;

    @DisplayName("부서 등록 - 성공")
    @Test
    void createDepartment_success() {
        // given
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .name("기획팀")
                .contact("010-1234-5678")
                .parentDeptId(1)
                .build();

        Department mapped = Department.builder()
                .name(request.getName())
                .contact(request.getContact())
                .parentDeptId(request.getParentDeptId())
                .build();

        Department parent = Department.builder()
                .deptId(1)
                .name("본부")
                .contact("010-1111-1111")
                .build();

        Department saved = Department.builder()
                .deptId(100)
                .name("기획팀")
                .contact("010-1234-5678")
                .parentDeptId(1)
                .build();

        // when
        when(departmentRepository.findById(1)).thenReturn(Optional.of(parent));
        when(modelMapper.map(request, Department.class)).thenReturn(mapped);
        when(departmentRepository.save(mapped)).thenReturn(saved);

        DepartmentCreateResponse response = departmentCommandService.createDepartment(request);

        // then
        assertThat(response.getDeptId()).isEqualTo(100);
    }

    @DisplayName("부서 등록 - 실패: 부모 부서 없음")
    @Test
    void createDepartment_fail_parentNotFound() {
        // given
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .name("기획팀")
                .contact("010-1234-5678")
                .parentDeptId(999)
                .build();

        Department mapped = Department.builder()
                .name(request.getName())
                .contact(request.getContact())
                .parentDeptId(request.getParentDeptId())
                .build();

        // when
        when(modelMapper.map(request, Department.class)).thenReturn(mapped);
        when(departmentRepository.findById(999)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> departmentCommandService.createDepartment(request))
                .isInstanceOf(DepartmentException.class)
                .hasMessageContaining(ErrorCode.DEPARTMENT_NOT_FOUND.getMessage());
    }



}