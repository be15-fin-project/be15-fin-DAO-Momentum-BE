package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.service.MyObjectionQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyObjectionQueryController.class)
class MyObjectionQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyObjectionQueryService service;

    @Test
    @DisplayName("본인 이의제기 내역 조회 성공")
    @WithMockUser(username = "1", roles = "USER")
    void getMyObjections_success() throws Exception {
        // given
        MyObjectionItemDto item = MyObjectionItemDto.builder()
                .objectionId(5001L)
                .resultId(501L)
                .createdAt("2025-06-22 17:31:08")
                .overallGrade("A")
                .statusType("PENDING")
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1L)
                .build();

        MyObjectionListResultDto resultDto =
                new MyObjectionListResultDto(List.of(item), pagination);

        given(service.getMyObjections(any(Long.class), any(MyObjectionListRequestDto.class)))
                .willReturn(resultDto);

        // when & then
        mockMvc.perform(get("/hr-objections/my")
                        .param("statusId", "1")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].objectionId").value(5001))
                .andExpect(jsonPath("$.data.content[0].resultId").value(501))
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2025-06-22 17:31:08"))
                .andExpect(jsonPath("$.data.content[0].overallGrade").value("A"))
                .andExpect(jsonPath("$.data.content[0].statusType").value("PENDING"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("본인 이의제기 상세 조회 성공")
    @WithMockUser(username = "1", roles = "USER")
    void getMyObjectionDetail_success() throws Exception {
        // given
        // 1) 기본 상세 정보
        ObjectionListResultDto base = ObjectionListResultDto.builder()
                .objectionId(5001L)
                .resultId(10001L)
                .empNo("20250001")
                .empName("김현우")
                .evaluatedAt("2025-06-22 17:31:08")
                .weightPerform(20)
                .weightTeam(15)
                .weightAttitude(15)
                .weightImmersion(25)
                .weightResult(20)
                .weightAdjust(5)
                .rateS(5)
                .rateA(20)
                .rateB(35)
                .rateC(30)
                .rateD(10)
                .objectionReason("점수가 낮습니다.")
                .status("PENDING")
                .responseReason(null)
                .build();

        // 2) 요인별 점수
        FactorScoreDto fs1 = FactorScoreDto.builder()
                .propertyName("커뮤니케이션")
                .score(88)
                .build();
        FactorScoreDto fs2 = FactorScoreDto.builder()
                .propertyName("조직협업")
                .score(92)
                .build();

        ObjectionDetailResultDto detailDto = ObjectionDetailResultDto.builder()
                .list(List.of(base))
                .factorScores(List.of(fs1, fs2))
                .build();

        given(service.getObjectionDetail(any(Long.class), any(Long.class)))
                .willReturn(detailDto);

        // when & then
        mockMvc.perform(get("/hr-objections/my/{id}", 5001L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // 기본 정보 확인
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].objectionId").value(5001))
                .andExpect(jsonPath("$.data.list[0].empNo").value("20250001"))
                .andExpect(jsonPath("$.data.list[0].empName").value("김현우"))
                // 요인별 점수 확인
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.factorScores[0].propertyName").value("커뮤니케이션"))
                .andExpect(jsonPath("$.data.factorScores[0].score").value(88))
                .andExpect(jsonPath("$.data.factorScores[1].propertyName").value("조직협업"))
                .andExpect(jsonPath("$.data.factorScores[1].score").value(92));
    }
}
