package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;
import com.dao.momentum.evaluation.hr.query.service.EvaluationHrService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationHrController.class)
class EvaluationHrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationHrService service;

    @Test
    @DisplayName("본인 인사평가 내역 조회 성공")
    @WithMockUser(username = "1", roles = "USER")
    void getMyHrEvaluations_success() throws Exception {
        // given
        HrEvaluationItemDto item1 = HrEvaluationItemDto.builder()
                .roundNo(5)
                .overallGrade("우수")
                .evaluatedAt(LocalDateTime.of(2025, 6, 15, 14, 23, 45))
                .build();

        FactorScoreDto fs1 = FactorScoreDto.builder()
                .propertyName("커뮤니케이션")
                .score(88)
                .build();
        FactorScoreDto fs2 = FactorScoreDto.builder()
                .propertyName("문제해결")
                .score(92)
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1L)
                .build();

        HrEvaluationListResultDto resultDto = HrEvaluationListResultDto.builder()
                .items(List.of(item1))
                .factorScores(List.of(fs1, fs2))
                .pagination(pagination)
                .build();

        given(service.getHrEvaluations(eq(1L), any(MyHrEvaluationListRequestDto.class)))
                .willReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluations/hr")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-06-30")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].roundNo").value(5))
                .andExpect(jsonPath("$.items[0].overallGrade").value("우수"))
                .andExpect(jsonPath("$.items[0].evaluatedAt").value("2025-06-15T14:23:45"))
                .andExpect(jsonPath("$.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.factorScores[*].propertyName", containsInAnyOrder("커뮤니케이션", "문제해결")))
                .andExpect(jsonPath("$.factorScores[*].score", containsInAnyOrder(88, 92)))
                .andExpect(jsonPath("$.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.pagination.totalPage").value(1))
                .andExpect(jsonPath("$.pagination.totalItems").value(1L));
    }
}
