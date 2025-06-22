package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.service.EvaluationQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(EvaluationQueryController.class)
class EvaluationQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationQueryService evaluationQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("사원 간 평가 결과 목록 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getPeerEvaluations_success() throws Exception {
        // given
        PeerEvaluationResponseDto dto = PeerEvaluationResponseDto.builder()
                .resultId(101L)
                .evalId(20250001L)
                .evalName("김현우")
                .targetId(20250002L)
                .targetName("정예준")
                .formName("동료 평가")
                .roundNo(2)
                .score(90)
                .reason("협업이 뛰어남")
                .createdAt(LocalDateTime.of(2025, 6, 19, 10, 0))
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        PeerEvaluationListResultDto resultDto = new PeerEvaluationListResultDto(List.of(dto), pagination);

        Mockito.when(evaluationQueryService.getPeerEvaluations(any(PeerEvaluationListRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/peer")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].evalName").value("김현우"))
                .andExpect(jsonPath("$.data.list[0].targetName").value("정예준"))
                .andExpect(jsonPath("$.data.list[0].formName").value("동료 평가"))
                .andExpect(jsonPath("$.data.list[0].roundNo").value(2))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }


    @Test
    @DisplayName("사원 간 평가 상세 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getPeerEvaluationDetail_success() throws Exception {
        // given
        Long resultId = 101L;

        PeerEvaluationDetailResponseDto detail = PeerEvaluationDetailResponseDto.builder()
                .resultId(resultId)
                .evalId(20250001L)
                .evalName("김현우")
                .targetId(20250002L)
                .targetName("정예준")
                .formName("동료 평가")
                .roundNo(2)
                .score(90)
                .reason("성실하고 꼼꼼함")
                .createdAt(LocalDateTime.of(2025, 6, 19, 10, 0))
                .build();

        List<FactorScoreDto> factorScores = List.of(
                FactorScoreDto.builder().propertyName("문제해결").score(95).build(),
                FactorScoreDto.builder().propertyName("성실성").score(90).build()
        );

        PeerEvaluationDetailResultDto resultDto = PeerEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();

        Mockito.when(evaluationQueryService.getPeerEvaluationDetail(anyLong()))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/peer/{resultId}", resultId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.detail.evalName").value("김현우"))
                .andExpect(jsonPath("$.data.detail.targetName").value("정예준"))
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.factorScores[0].propertyName").value("문제해결"))
                .andDo(print());
    }
}
