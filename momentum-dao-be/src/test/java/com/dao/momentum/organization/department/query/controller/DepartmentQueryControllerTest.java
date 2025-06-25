package com.dao.momentum.organization.department.query.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.service.DepartmentQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentQueryController.class)
@ExtendWith(SpringExtension.class)
class DepartmentQueryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentQueryService departmentQueryService;

    @Test
    @DisplayName("[Controller] 부서 정보 조회_성공")
    @WithMockUser(username = "1", roles = "EMPLOYEE")
    void getDepartmentsInfo_success() throws Exception {
        // given
        DepartmentInfoDTO deptA = DepartmentInfoDTO.builder()
                .deptId(1)
                .name("본사")
                .parentDeptId(null)
                .build();

        DepartmentsInfoResponse mockResponse = DepartmentsInfoResponse.builder()
                .departmentInfoDTOList(List.of(deptA))
                .build();

        when(departmentQueryService.getDepartmentsInfo()).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.departmentInfoDTOList[0].name").value("본사"));
    }

    @Test
    @DisplayName("[Controller] 부서 정보 조회_DEPARTMENT_NOT_FOUND")
    @WithMockUser(username = "1", roles = "EMPLOYEE")
    void getDepartmentsInfo_shouldReturnBadRequest_whenDepartmentExceptionOccurs() throws Exception {
        // given
        when(departmentQueryService.getDepartmentsInfo())
                .thenThrow(new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/departments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("시스템 오류입니다."));
    }

}