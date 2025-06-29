package com.dao.momentum.approve.query.controller;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.DraftApproveDTO;
import com.dao.momentum.approve.query.dto.EmployeeLeaderDto;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.dto.response.DraftApproveResponse;
import com.dao.momentum.approve.query.service.ApproveQueryService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApproveQueryController.class)
@AutoConfigureMockMvc
class ApproveQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApproveQueryService approveQueryService;

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

        Mockito.when(approveQueryService.getReceivedApprove(Mockito.any(), Mockito.any() ,Mockito.any()))
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

    @DisplayName("보낸 결재 목록 가져오기")
    @WithMockUser(username = "1")
    @Test
    void getDraftApprovalTest() throws Exception {

        List<DraftApproveDTO> dummyList = getDummyDraftApproves();

        DraftApproveResponse approveResponse = DraftApproveResponse.builder()
                .draftApproveDTO(dummyList)
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(dummyList.size())
                        .build())
                .build();

        Mockito.when(approveQueryService.getDraftApprove(Mockito.any(), Mockito.any() ,Mockito.any()))
                .thenReturn(approveResponse);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/approval/documents/draft")
                                .param("tab", "RECEIPT")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.draftApproveDTO[0].approveTitle").value("점심 식사 영수증"))
                .andExpect(jsonPath("$.data.draftApproveDTO[1].approveTitle").value("출장 택시비"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

    }
    @WithMockUser(username = "1")
    @DisplayName("사원의 팀장 조회 테스트")
    @Test
    void testGetEmployeeLeader() throws Exception {
        EmployeeLeaderDto employeeLeaderDto = EmployeeLeaderDto.builder()
                .teamLeaderId(2L)
                .teamLeaderName("정유진 과장님")
                .build();

        Mockito.when(approveQueryService.getEmployeeLeader(Mockito.any()))
                .thenReturn(employeeLeaderDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/approval/leader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.teamLeaderId").value(2L))
                .andExpect(jsonPath("$.data.teamLeaderName").value("정유진 과장님"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

    }

    /* 받은 결재함 조회 시 사용하는 더미 데이터 */
    private List<ApproveDTO> getDummyApproves() {
        ApproveDTO dummy1 = ApproveDTO.builder()
                .approveId(1L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(1L)
                .approveTitle("점심 식사 영수증")
                .approveType(ApproveType.RECEIPT)
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
                .approveType(ApproveType.RECEIPT)
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .build();

        return List.of(dummy1, dummy2);
    }

    /* 보낸 결재함 조회 시 사용하는 더미 데이터 */
    private List<DraftApproveDTO> getDummyDraftApproves() {
        DraftApproveDTO dummy1 = DraftApproveDTO.builder()
                .approveId(1L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .approveTitle("점심 식사 영수증")
                .approveType("RECEIPT")
                .createAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .completeAt(null)
                .build();

        DraftApproveDTO dummy2 = DraftApproveDTO.builder()
                .approveId(2L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .approveTitle("출장 택시비")
                .approveType("RECEIPT")
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .build();

        return List.of(dummy1, dummy2);
    }
}