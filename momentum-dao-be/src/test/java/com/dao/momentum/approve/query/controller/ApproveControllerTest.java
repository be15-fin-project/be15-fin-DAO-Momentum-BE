package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.service.ApproveService;
import com.dao.momentum.common.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(1L)
                .approveTitle("점심 식사 영수증")
                .approveType("RECEIPT")
                .createAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .completeAt(null)
                .employeeName("장도윤")
                .departmentName("백엔드팀")
                .build();

        ApproveDTO dummy2 = ApproveDTO.builder()
                .approveId(2L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(2L)
                .approveTitle("출장 택시비")
                .approveType("RECEIPT")
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .build();

        return List.of(dummy1, dummy2);
    }

    @DisplayName("받은 결재 목록 가져오기")
    @WithMockUser(username = "1") // 임의로 인증 정보 넣기
    @Test
    void getReceivedApprovalTest() throws Exception {

        List<ApproveDTO> dummyList = getDummyApproves();

        ApproveResponse approveResponse = ApproveResponse.builder()
                .approveDTO(dummyList)
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(dummyList.size())
                        .build())
                .build();

        Mockito.when(approveService.getReceivedApprove(Mockito.any(), Mockito.any() ,Mockito.any()))
                .thenReturn(approveResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/approval/documents/received")
                                .param("tab", "RECEIPT")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.approveDTO[0].approveTitle").value("점심 식사 영수증"))
                .andExpect(jsonPath("$.data.approveDTO[1].approveTitle").value("출장 택시비"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

    }
}