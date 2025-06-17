package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.AdminApproveService;
import com.dao.momentum.common.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminApproveController.class)
@AutoConfigureMockMvc
class AdminApproveControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AdminApproveService adminApproveService;

    private List<ApproveDTO> getDummyApproves() {
        ApproveDTO dummy1 = ApproveDTO.builder()
                .approveId(1L)
                .parentApproveId(null)
                .statusType("RECEIPT")
                .empId(1L)
                .approveTitle("연차 신청")
                .approveType("OVERTIME")
                .createAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .completeAt(null)
                .employeeName("장도윤")
                .departmentName("백엔드팀")
                .build();

        ApproveDTO dummy2 = ApproveDTO.builder()
                .approveId(2L)
                .parentApproveId(null)
                .statusType("RECEIPT")
                .empId(2L)
                .approveTitle("프로젝트 제안서 제출")
                .approveType("REMOTEWORK")
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .build();

        List<ApproveDTO> approveList = List.of(dummy1, dummy2);

        return approveList;
    }

    @DisplayName("전체 결재 목록 가져오기")
    @WithMockUser(authorities = "MASTER")
    @Test
    void getFollowListByMemberUidTest() throws Exception {

        ApproveListRequest request = ApproveListRequest.builder()
                .tab("RECEIPT")
                .build();

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

        Mockito.when(adminApproveService.getApproveList(request, pageRequest))
                .thenReturn(approveResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/admin/approval/documents")
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
