package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.service.MyObjectionQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
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
    @DisplayName("이의제기 상세 조회 성공")
    @WithMockUser(username = "1", roles = "USER")
    void getObjectionDetail_success() throws Exception {
        // given
        ObjectionItemDto item = ObjectionItemDto.builder()
                .objectionId(1L)
                .resultId(17L)
                .empNo("20250004")
                .empName("김현우")
                .evaluatedAt("2025-01-21 00:00:00")
                .objectionReason("인사 평가 점수가 예상보다 낮게 나왔습니다.")
                .statusType("PENDING")
                .responseReason(null)
                .build();

        WeightInfo weightInfo = WeightInfo.builder()
                .weightPerform(25)
                .weightTeam(20)
                .weightAttitude(15)
                .weightGrowth(10)
                .weightEngagement(15)
                .weightResult(15)
                .build();

        RateInfo rateInfo = RateInfo.builder()
                .rateS(15)
                .rateA(25)
                .rateB(30)
                .rateC(20)
                .rateD(10)
                .build();

        List<FactorScoreDto> scores = List.of(
                FactorScoreDto.builder().propertyName("업무 수행 역량").score("B").build(),
                FactorScoreDto.builder().propertyName("문제 해결 능력").score("A").build()
        );

        ObjectionDetailResultDto resultDto = ObjectionDetailResultDto.builder()
                .itemDto(item)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .factorScores(scores)
                .build();

        given(service.getObjectionDetail(1L)).willReturn(resultDto);

        // when & then
        mockMvc.perform(get("/hr-objections/my/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemDto.empName").value("김현우"))
                .andExpect(jsonPath("$.data.itemDto.resultId").value(17))
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.factorScores[*].propertyName",
                        containsInAnyOrder("업무 수행 역량", "문제 해결 능력")))
                .andExpect(jsonPath("$.data.weightInfo.weightPerform").value(25))
                .andExpect(jsonPath("$.data.rateInfo.rateS").value(15));
    }
}
