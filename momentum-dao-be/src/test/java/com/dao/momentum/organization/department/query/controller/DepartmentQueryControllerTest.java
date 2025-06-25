package com.dao.momentum.organization.department.query.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentInfoDTO;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.service.DepartmentQueryService;
import com.dao.momentum.organization.employee.query.dto.response.DepartmentMemberDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("부서 정보 조회_성공")
    @WithMockUser(username = "1", roles = "EMPLOYEE")
    void getDepartmentDetails_success() throws Exception{
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

        DepartmentDetailResponse response = DepartmentDetailResponse.builder()
                .departmentDetailDTO(departmentDetailDTO)
                .departmentMemberDTOList(departmentMemberDTOList)
                .build();

        when(departmentQueryService.getDepartmentDetails(deptId)).thenReturn(response);

        mockMvc.perform(get("/departments/{deptId}", deptId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.departmentDetailDTO.name").value("경영지원본부"))
                .andExpect(jsonPath("$.data.departmentDetailDTO.contact").value("000-0001-0001"))
                .andExpect(jsonPath("$.data.departmentDetailDTO.createdAt").value("2025-06-13"))

                // 멤버 1 검증
                .andExpect(jsonPath("$.data.departmentMemberDTOList[0].name").value("홍길동"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[0].contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[0].email").value("employee1@exmaple.com"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[0].position").value("부장"))

                // 멤버 2 검증
                .andExpect(jsonPath("$.data.departmentMemberDTOList[1].name").value("강감찬"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[1].contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[1].email").value("employee2@exmaple.com"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[1].position").value("대리"))

                // 멤버 3 검증
                .andExpect(jsonPath("$.data.departmentMemberDTOList[2].name").value("홍길동"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[2].contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[2].email").value("employee3@exmaple.com"))
                .andExpect(jsonPath("$.data.departmentMemberDTOList[2].position").value("사원"))

                .andDo(print());
    }

    @Test
    @DisplayName("부서 정보 조회_실패")
    @WithMockUser(username = "1", roles = "EMPLOYEE")
    void getDepartmentDetails_fail() throws Exception{
        int deptId = 999;

        when(departmentQueryService.getDepartmentDetails(deptId)).thenThrow(new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));
        mockMvc.perform(get("/departments/{deptId}", deptId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("11002"))
                .andExpect(jsonPath("$.message").value("시스템 오류입니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }
}