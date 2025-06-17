package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.AdminApproveService;
import com.dao.momentum.approve.query.service.ApproveService;
import com.dao.momentum.common.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApproveController.class)
@AutoConfigureMockMvc
class ApproveControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApproveService approveService;

    private List<ApproveDTO> getDummyApproves() {
        ApproveDTO dummy1 = ApproveDTO.builder()
                .approveId(1L)
                .approveTitle("점심 비용 신청")
                .approveType("RECEIPT")
                .empId(1L)
                .employeeName("장도윤")
                .departmentName("백엔드팀")
                .createAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .build();

        ApproveDTO dummy2 = ApproveDTO.builder()
                .approveId(2L)
                .approveTitle("저녁 비용 신청")
                .approveType("RECEIPT")
                .empId(2L)
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .createAt(LocalDateTime.of(2025, 6, 2, 0, 0))
                .build();

        return List.of(dummy1, dummy2);
    }

    @DisplayName("받은 결재 목록 가져오기")
    @WithMockUser(username = "1") // 임의로 인증 정보 넣기
    @Test
    void getReceivedApprovalTest() throws Exception {
        ApproveListRequest request = ApproveListRequest.builder()
                .tab("RECEIPT")
                .build();

        Long empId = 1L;

        PageRequest pageRequest = new PageRequest(1, 10);

        List<ApproveDTO> dummyList = getDummyApproves();

        ApproveResponse approveResponse = ApproveResponse.builder()
                .approveDTO(dummyList)
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(dummyList.size())
                        .build())
                .build();

        Mockito.when(approveService.getReceivedApprove(request, empId, pageRequest))
                .thenReturn(approveResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/approval/documents/received")
                                .param("tab", "RECEIPT")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

    }
}