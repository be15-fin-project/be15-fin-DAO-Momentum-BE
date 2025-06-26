package com.dao.momentum.organization.employee.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.organization.employee.query.dto.request.AppointSearchDTO;
import com.dao.momentum.organization.employee.query.dto.request.AppointSearchRequest;
import com.dao.momentum.organization.employee.query.dto.request.EmployeeSearchDTO;
import com.dao.momentum.organization.employee.query.dto.request.EmployeeSearchRequest;
import com.dao.momentum.organization.employee.query.dto.response.*;
import com.dao.momentum.organization.employee.query.mapper.AdminEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeQueryService {

    private final AdminEmployeeMapper adminEmployeeMapper;

    @Transactional(readOnly = true)
    public EmployeeListResponse getEmployees(EmployeeSearchRequest employeeSearchRequest) {
        EmployeeSearchDTO employeeSearchDTO = EmployeeSearchRequest.fromRequest(employeeSearchRequest);

        List<EmployeeSummaryDTO> employees = adminEmployeeMapper.getEmployees(employeeSearchDTO);

        int page = employeeSearchDTO.getPage();
        int size = employeeSearchDTO.getSize();

        long totalItems = adminEmployeeMapper.countEmployees(employeeSearchDTO);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        return EmployeeListResponse.builder()
                .employees(employees)
                .pagination(
                        Pagination.builder()
                                .currentPage(page)
                                .totalItems(totalItems)
                                .totalPage(totalPages)
                                .build()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public EmployeeDetailsResponse getEmployeeDetails(long empId) {
        EmployeeDTO employeeDetails = adminEmployeeMapper.getEmployeeDetails(empId);
        List<EmployeeRecordsDTO> employeeRecords = adminEmployeeMapper.getEmployeeRecords(empId);

        return EmployeeDetailsResponse.builder()
                .employeeDetails(employeeDetails)
                .employeeRecords(employeeRecords)
                .build();
    }

    @Transactional(readOnly = true)
    public AppointListResponse getAppoints(AppointSearchRequest appointSearchRequest) {
        AppointSearchDTO appointSearchDTO = AppointSearchDTO.fromRequest(appointSearchRequest);

        List<AppointDTO> appoints = adminEmployeeMapper.getAppoints(appointSearchDTO);
        long totalItems = adminEmployeeMapper.countAppoints(appointSearchDTO);

        int currentPage = appointSearchDTO.getPage();
        int size = appointSearchDTO.getSize();

        return AppointListResponse.builder()
                .appoints(appoints)
                .pagination(
                        Pagination.builder()
                                .currentPage(currentPage)
                                .totalItems(totalItems)
                                .totalPage((int) Math.ceil((double) totalItems / size))
                                .build()
                )
                .build();
    }
}
