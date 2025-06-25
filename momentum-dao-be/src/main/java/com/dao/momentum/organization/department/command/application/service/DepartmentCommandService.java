package com.dao.momentum.organization.department.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentCommandService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    public DepartmentCreateResponse createDepartment(DepartmentCreateRequest request) {
        Department department = modelMapper.map(request, Department.class);

        departmentRepository.findById(department.getParentDeptId()).orElseThrow(
                () -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND)
        );

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentCreateResponse.builder()
                .deptId(savedDepartment.getDeptId())
                .build();
    }
}
