package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
                .evalNo(20250001L)
                .evalName("김현우")
                .targetNo(20250002L)
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

        PeerEvaluationResponseDto detail = PeerEvaluationResponseDto.builder()
                .resultId(resultId)
                .evalNo(20250001L)
                .evalName("김현우")
                .targetNo(20250002L)
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

    @Test
    @DisplayName("조직 평가 목록 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getOrgEvaluations_success() throws Exception {
        // given
        OrgEvaluationResponseDto dto = OrgEvaluationResponseDto.builder()
                .formName("조직 몰입도")
                .roundNo(2)
                .score(85)
                .build();

        OrgEvaluationListResultDto resultDto = new OrgEvaluationListResultDto(Collections.singletonList(dto), null);
        given(evaluationQueryService.getOrgEvaluations(any(OrgEvaluationListRequestDto.class)))
                .willReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/org")
                        .param("formId", "5")
                        .param("roundId", "2")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list[0].formName").value("조직 몰입도"))
                .andExpect(jsonPath("$.data.list[0].roundNo").value(2))
                .andExpect(jsonPath("$.data.list[0].score").value(85));
    }

    @Test
    @DisplayName("조직 평가 상세 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getOrgEvaluationDetail_success() throws Exception {
        // given
        Long resultId = 501L;

        OrgEvaluationResponseDto detail = OrgEvaluationResponseDto.builder()
                .resultId(resultId)
                .empNo(20250001L)
                .evalName("김현우")
                .formName("조직 몰입도")
                .roundNo(2)
                .score(87)
                .createdAt(LocalDateTime.of(2025, 6, 20, 15, 0))
                .build();

        List<FactorScoreDto> factorScores = List.of(
                FactorScoreDto.builder().propertyName("몰입도").score(88).build(),
                FactorScoreDto.builder().propertyName("책임감").score(86).build()
        );

        OrgEvaluationDetailResultDto resultDto = OrgEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();

        Mockito.when(evaluationQueryService.getOrgEvaluationDetail(anyLong()))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/org/{resultId}", resultId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.detail.evalName").value("김현우"))
                .andExpect(jsonPath("$.data.detail.formName").value("조직 몰입도"))
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.factorScores[0].propertyName").value("몰입도"))
                .andExpect(jsonPath("$.data.factorScores[1].score").value(86))
                .andDo(print());
    }

    @Test
    @DisplayName("자가 진단 평가 내역 조회 성공")
    @WithMockUser(authorities = "MASTER") // or HR_MANAGER
    void getSelfEvaluations_success() throws Exception {
        // given
        SelfEvaluationResponseDto dto = SelfEvaluationResponseDto.builder()
                .resultId(201L)
                .empNo(20250001L)
                .evalName("김하진")
                .formName("직업 만족도 진단")
                .roundNo(1)
                .score(82)
                .reason("업무 만족도는 보통 이상")
                .createdAt(LocalDateTime.of(2025, 6, 21, 14, 30))
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        SelfEvaluationListResultDto resultDto = new SelfEvaluationListResultDto(List.of(dto), pagination);

        Mockito.when(evaluationQueryService.getSelfEvaluations(any()))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/self")
                        .param("empNo", "20250001")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].empNo").value("20250001"))
                .andExpect(jsonPath("$.data.list[0].evalName").value("김하진"))
                .andExpect(jsonPath("$.data.list[0].formName").value("직업 만족도 진단"))
                .andExpect(jsonPath("$.data.list[0].roundNo").value(1))
                .andExpect(jsonPath("$.data.list[0].score").value(82))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("자가 진단 평가 상세 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getSelfEvaluationDetail_success() throws Exception {
        // given
        Long resultId = 301L;

        SelfEvaluationResponseDto detail = SelfEvaluationResponseDto.builder()
                .resultId(resultId)
                .empNo(20250001L)
                .evalName("김하진")
                .formName("직무 스트레스 자가진단")
                .roundNo(1)
                .score(75)
                .reason("스트레스 요인 일부 존재")
                .createdAt(LocalDateTime.of(2025, 6, 22, 9, 30))
                .build();

        List<FactorScoreDto> factorScores = List.of(
                FactorScoreDto.builder().propertyName("스트레스 반응").score(78).build(),
                FactorScoreDto.builder().propertyName("스트레스 요인").score(72).build()
        );

        SelfEvaluationDetailResultDto resultDto = SelfEvaluationDetailResultDto.builder()
                .detail(detail)
                .factorScores(factorScores)
                .build();

        Mockito.when(evaluationQueryService.getSelfEvaluationDetail(anyLong()))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluation/results/self/{resultId}", resultId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.detail.evalName").value("김하진"))
                .andExpect(jsonPath("$.data.detail.formName").value("직무 스트레스 자가진단"))
                .andExpect(jsonPath("$.data.detail.score").value(75))
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.factorScores[0].propertyName").value("스트레스 반응"))
                .andExpect(jsonPath("$.data.factorScores[1].score").value(72))
                .andDo(print());
    }

}
