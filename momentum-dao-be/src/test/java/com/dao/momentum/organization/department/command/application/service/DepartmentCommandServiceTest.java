package com.dao.momentum.organization.department.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentUpdateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentDeleteResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentUpdateResponse;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.aggregate.DeptHead;
import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.command.domain.repository.DeptHeadRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentCommandServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DeptHeadRepository deptHeadRepository;
    @Mock
    private EmployeeRepository employeeRepository;
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

    @Test
    void updateDepartment_successfulUpdateWithDeptHead() {
        // Given
        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .deptId(1)
                .parentDeptId(2)
                .deptHeadId(100L)
                .build();

        Department mockDepartment = mock(Department.class);
        DeptHead mockDeptHead = DeptHead.builder()
                .deptId(1)
                .empId(100L)
                .build();

        when(departmentRepository.findById(1)).thenReturn(Optional.of(mockDepartment));
        when(departmentRepository.existsByDeptIdAndIsDeleted(2, IsDeleted.N)).thenReturn(true);
        when(departmentRepository.isSubDepartment(1, 2)).thenReturn(0);
        when(employeeRepository.existsByEmpIdAndDeptId(100L, 1)).thenReturn(true);
        when(deptHeadRepository.findByDeptId(1)).thenReturn(Optional.of(mockDeptHead));

        // When
        DepartmentUpdateResponse response = departmentCommandService.updateDepartment(request);

        // Then
        assertNotNull(response);
        verify(mockDepartment).update(request);
        verify(deptHeadRepository).save(any(DeptHead.class));
    }

    @Test
    void updateDepartment_throwsExceptionIfDepartmentNotFound() {
        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .deptId(99)
                .build();

        when(departmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(DepartmentException.class, () -> departmentCommandService.updateDepartment(request));
    }

    @Test
    void updateDepartment_throwsIfParentIsSelf() {
        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .deptId(5)
                .parentDeptId(5)
                .build();

        Department department = mock(Department.class);
        when(department.getDeptId()).thenReturn(5);
        when(departmentRepository.findById(5)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByDeptIdAndIsDeleted(5, IsDeleted.N)).thenReturn(true);

        assertThrows(DepartmentException.class, () -> departmentCommandService.updateDepartment(request));
    }

    @Test
    void updateDepartment_removeDeptHead() {
        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .deptId(10)
                .deptHeadId(null)
                .build();

        Department department = mock(Department.class);
        when(departmentRepository.findById(10)).thenReturn(Optional.of(department));

        DepartmentUpdateResponse response = departmentCommandService.updateDepartment(request);

        assertNotNull(response);
        verify(deptHeadRepository).deleteByDeptId(10);
    }

    @Test
    void updateDepartment_invalidDeptHeadThrowsException() {
        DepartmentUpdateRequest request = DepartmentUpdateRequest.builder()
                .deptId(1)
                .deptHeadId(999L)
                .build();

        Department department = mock(Department.class);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByEmpIdAndDeptId(999L, 1)).thenReturn(false);

        assertThrows(EmployeeException.class, () -> departmentCommandService.updateDepartment(request));
    }

    @Test
    void deleteDepartment_successfulDeletion() {
        // Given
        int deptId = 10;
        Department mockDepartment = mock(Department.class);

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(mockDepartment));
        when(employeeRepository.existsByDeptIdAndStatusIsNot(deptId, Status.RESIGNED)).thenReturn(false);
        when(departmentRepository.existsByParentDeptIdAndIsDeleted(deptId, IsDeleted.N)).thenReturn(false);

        // When
        DepartmentDeleteResponse response = departmentCommandService.deleteDepartment(deptId);

        // Then
        assertNotNull(response);
        assertEquals(deptId, response.getDeptId());
        verify(mockDepartment).delete();
        verify(departmentRepository).save(mockDepartment);
    }

    @Test
    void deleteDepartment_throwsIfDepartmentNotFound() {
        int deptId = 99;
        when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

        assertThrows(DepartmentException.class, () -> departmentCommandService.deleteDepartment(deptId));
    }

    @Test
    void deleteDepartment_throwsIfDepartmentHasActiveEmployees() {
        int deptId = 11;
        Department mockDepartment = mock(Department.class);

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(mockDepartment));
        when(employeeRepository.existsByDeptIdAndStatusIsNot(deptId, Status.RESIGNED)).thenReturn(true);

        assertThrows(DepartmentException.class, () -> departmentCommandService.deleteDepartment(deptId));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartment_throwsIfHasChildDepartments() {
        int deptId = 12;
        Department mockDepartment = mock(Department.class);

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(mockDepartment));
        when(employeeRepository.existsByDeptIdAndStatusIsNot(deptId, Status.RESIGNED)).thenReturn(false);
        when(departmentRepository.existsByParentDeptIdAndIsDeleted(deptId, IsDeleted.N)).thenReturn(true);

        assertThrows(DepartmentException.class, () -> departmentCommandService.deleteDepartment(deptId));
        verify(departmentRepository, never()).save(any());
    }
}
