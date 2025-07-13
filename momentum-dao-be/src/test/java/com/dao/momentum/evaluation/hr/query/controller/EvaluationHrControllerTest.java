package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.service.EvaluationHrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationHrController.class)
class EvaluationHrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationHrService evaluationHrService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("인사 평가 내역 조회 - 성공")
    @WithMockUser(username = "53")
    void getMyHrEvaluations_success() throws Exception {
        HrEvaluationItemDto item = HrEvaluationItemDto.builder()
                .resultId(1001L)
                .roundNo(3)
                .overallGrade("우수")
                .evaluatedAt(LocalDateTime.now())
                .objectionSubmitted(false)
                .build();

        HrEvaluationWithFactorsDto wrappedItem = HrEvaluationWithFactorsDto.builder()
                .item(item)
                .factorScores(Collections.emptyList())
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        HrEvaluationListResultDto mockResult = HrEvaluationListResultDto.builder()
                .items(List.of(wrappedItem))
                .pagination(pagination)
                .build();

        given(evaluationHrService.getHrEvaluations(eq(53L), any(MyHrEvaluationListRequestDto.class)))
                .willReturn(mockResult);

        mockMvc.perform(get("/evaluations/hr")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].item.resultId").value(1001))
                .andExpect(jsonPath("$.data.items[0].item.roundNo").value(3))
                .andExpect(jsonPath("$.data.items[0].item.overallGrade").value("우수"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andExpect(jsonPath("$.data.pagination.currentPage").value(1));
    }


    @Test
    @DisplayName("인사 평가 상세 조회 성공")
    @WithMockUser(username = "53")
    void getHrEvaluationDetail_success() throws Exception {
        HrEvaluationDetailResultDto mockResult = HrEvaluationDetailResultDto.builder()
                .content(HrEvaluationDetailDto.builder()
                        .resultId(1001L)
                        .empNo("20250001")
                        .empName("김현우")
                        .overallGrade("우수")
                        .evaluatedAt(LocalDateTime.now())
                        .build())
                .rateInfo(RateInfo.builder().rateS(5).rateA(20).rateB(30).rateC(30).rateD(15).build())
                .weightInfo(WeightInfo.builder()
                        .weightPerform(20).weightTeam(20).weightAttitude(20)
                        .weightGrowth(15).weightEngagement(15).weightResult(10).build())
                .factorScores(Collections.emptyList())
                .build();

        given(evaluationHrService.getHrEvaluationDetail(eq(53L), eq(1001L)))
                .willReturn(mockResult);

        mockMvc.perform(get("/evaluations/hr/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.resultId").value(1001));
    }

    @Test
    @DisplayName("인사 평가 기준 조회 성공")
    @WithMockUser(roles = {"ADMIN"})
    void getEvaluationCriteria_success() throws Exception {
        HrEvaluationCriteriaDto mockResult = HrEvaluationCriteriaDto.builder()
                .rateInfo(RateInfo.builder().rateS(5).rateA(20).rateB(30).rateC(30).rateD(15).build())
                .weightInfo(WeightInfo.builder()
                        .weightPerform(20).weightTeam(20).weightAttitude(20)
                        .weightGrowth(15).weightEngagement(15).weightResult(10).build())
                .build();

        given(evaluationHrService.getEvaluationCriteria(eq(3)))
                .willReturn(mockResult);

        mockMvc.perform(get("/evaluations/hr/3/criteria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rateInfo.rateA").value(20))
                .andExpect(jsonPath("$.data.weightInfo.weightResult").value(10));
    }
}
