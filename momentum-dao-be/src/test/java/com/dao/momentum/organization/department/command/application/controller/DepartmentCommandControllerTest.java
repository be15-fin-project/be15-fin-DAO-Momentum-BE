package com.dao.momentum.organization.department.command.application.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.application.service.DepartmentCommandService;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.department.query.controller.DepartmentQueryController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentCommandController.class)
@ExtendWith(SpringExtension.class)
class DepartmentCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentCommandService departmentCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("부서 등록 - 성공")
    @Test
    @WithMockUser(username = "1", authorities = {"MASTER"})
    void createDepartment_success() throws Exception {
        // given
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .name("기획팀")
                .contact("010-1234-5678")
                .parentDeptId(1)
                .build();

        DepartmentCreateResponse response = DepartmentCreateResponse.builder()
                .deptId(100)
                .build();

        when(departmentCommandService.createDepartment(any(DepartmentCreateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deptId").value(100));
    }

    @DisplayName("부서 등록 - 실패: 유효성 검사 실패")
    @Test
    @WithMockUser(username = "1", authorities = {"MASTER"})
    void createDepartment_validationFail() throws Exception {
        // given: name 필드가 비어 있음
        DepartmentCreateRequest invalidRequest = DepartmentCreateRequest.builder()
                .name("")  // @NotBlank 위반
                .contact("010-1234-5678")
                .parentDeptId(1)
                .build();

        // when & then
        mockMvc.perform(post("/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("부서 등록 - 실패: 부모 부서 없음")
    @Test
    @WithMockUser(username = "1", authorities = {"MASTER"})
    void createDepartment_parentNotFound() throws Exception {
        // given
        DepartmentCreateRequest request = DepartmentCreateRequest.builder()
                .name("기획팀")
                .contact("010-1234-5678")
                .parentDeptId(999)
                .build();

        when(departmentCommandService.createDepartment(any()))
                .thenThrow(new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


}
