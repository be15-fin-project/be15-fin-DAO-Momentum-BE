package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundResponseDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(EvaluationManageController.class)
class EvaluationManageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationManageService evaluationManageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("다면 평가 회차 목록 조회 성공")
    @WithMockUser(authorities = {"MASTER", "HR_MANAGER"})
    void getEvaluationRounds_success() throws Exception {
        // given
        EvaluationRoundResponseDto dto = EvaluationRoundResponseDto.builder()
                .roundId(1)
                .roundNo(3)
                .startAt(LocalDate.of(2025, 7, 1))
                .endAt(LocalDate.of(2025, 7, 8))
                .participantCount(25)
                .status(EvaluationRoundStatus.IN_PROGRESS)
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        EvaluationRoundListResultDto resultDto = new EvaluationRoundListResultDto(List.of(dto), pagination);

        Mockito.when(evaluationManageService.getEvaluationRounds(Mockito.any(EvaluationRoundListRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/evaluations/rounds")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-31")
                        .param("status", "IN_PROGRESS")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].roundNo").value(3))
                .andExpect(jsonPath("$.data.list[0].participantCount").value(25))
                .andExpect(jsonPath("$.data.list[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }
}
