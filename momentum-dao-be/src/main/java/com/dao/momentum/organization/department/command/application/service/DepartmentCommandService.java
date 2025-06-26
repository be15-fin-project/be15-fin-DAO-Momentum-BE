package com.dao.momentum.organization.department.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentUpdateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentDeleteResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentUpdateDTO;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentUpdateResponse;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.aggregate.DeptHead;
import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.command.domain.repository.DeptHeadRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentCommandService {
    private final DepartmentRepository departmentRepository;
    private final DeptHeadRepository deptHeadRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public DepartmentCreateResponse createDepartment(DepartmentCreateRequest request) {
        Department department = modelMapper.map(request, Department.class);
        if(department.getParentDeptId()!=null){
            departmentRepository.findById(department.getParentDeptId()).orElseThrow(
                    () -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND)
            );
        }

        Department savedDepartment = departmentRepository.save(department);

        return DepartmentCreateResponse.builder()
                .deptId(savedDepartment.getDeptId())
                .build();
    }

    @Transactional
    public DepartmentUpdateResponse updateDepartment(DepartmentUpdateRequest request) {
        Integer deptId = request.getDeptId();
        Integer parentDeptId = request.getParentDeptId();
        Long empId = request.getDeptHeadId();
        //부서 검색
        Department department = departmentRepository.findById(deptId).orElseThrow(
                () -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND)
        );
        if(parentDeptId!=null){
            //상위 부서 존재 확인
            if(!departmentRepository.existsByDeptIdAndIsDeleted(parentDeptId, IsDeleted.N)) {
                throw new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND);
            }

            //데이터 정합성 확인(본인 or 하위 부서를 부모로 하려는지)
            if(department.getDeptId().equals(parentDeptId)
                    || departmentRepository.isSubDepartment(deptId,parentDeptId)==1){
                throw new DepartmentException(ErrorCode.INVALID_PARENT_DEPT);
            }
        }

        //부서장 정보
        if(empId==null){
            deptHeadRepository.deleteByDeptId(deptId);
        }else{
            //사원이 해당 부서에 속해있는지 확인
            if(!employeeRepository.existsByEmpIdAndDeptId(empId,deptId)){
                throw new EmployeeException(ErrorCode.INVALID_DEPT_HEAD);
            }
            Optional<DeptHead> optional = deptHeadRepository.findByDeptId(request.getDeptId());
            if (optional.isPresent()) {
                // update
                DeptHead deptHead = optional.get();
                deptHead.setEmpId(request.getDeptHeadId()); // 엔티티 수정
                deptHeadRepository.save(deptHead);
            } else {
                // insert
                DeptHead newHead = DeptHead.builder()
                        .deptId(request.getDeptId())
                        .empId(request.getDeptHeadId())
                        .build();
                deptHeadRepository.save(newHead);
            }
        }

        //부서 수정
        department.update(request);

        DepartmentUpdateDTO dto = DepartmentUpdateDTO.from(request);

        return DepartmentUpdateResponse.builder()
                .departmentUpdateDTO(dto)
                .build();
    }

    @Transactional
    public DepartmentDeleteResponse deleteDepartment(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(
                () -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND)
        );

        //퇴사자가 아닌 사원들이 속해있는지 검사
        if(employeeRepository.existsByDeptIdAndStatusIsNot(deptId, Status.RESIGNED)){
            throw new DepartmentException(ErrorCode.DEPARTMENT_NOT_EMPTY);
        }

        //하위 부서가 존재하는지 검사
        if(departmentRepository.existsByParentDeptIdAndIsDeleted(deptId,IsDeleted.N)){
            throw new DepartmentException(ErrorCode.DEPARTMENT_HAS_CHILD);
        }

        department.delete();

        departmentRepository.save(department);

        return DepartmentDeleteResponse.builder()
                .deptId(deptId)
                .build();
    }
}
