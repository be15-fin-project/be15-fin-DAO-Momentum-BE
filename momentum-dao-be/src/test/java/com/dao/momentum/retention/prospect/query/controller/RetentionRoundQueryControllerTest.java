package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResponseDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.prospect.query.service.RetentionRoundQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(RetentionRoundQueryController.class)
class RetentionRoundQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetentionRoundQueryService retentionRoundQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("근속 회차 목록 조회 성공")
    @WithMockUser(authorities = "HR")
    void getRetentionRounds_success() throws Exception {
        // given
        RetentionRoundListResponseDto round = RetentionRoundListResponseDto.builder()
                .roundId(1)
                .roundNo(5)
                .year(2025)
                .month(6)
                .periodStart("2025-06-01")
                .periodEnd("2025-06-30")
                .participantCount(32)
                .build();

        RetentionRoundListResultDto resultDto = RetentionRoundListResultDto.builder()
                .content(List.of(round))
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(1L)
                        .build())
                .build();

        Mockito.when(retentionRoundQueryService.getRetentionRounds(any(RetentionRoundSearchRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/retention/rounds")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].roundId").value(1))
                .andExpect(jsonPath("$.data.content[0].roundNo").value(5))
                .andExpect(jsonPath("$.data.content[0].year").value(2025))
                .andExpect(jsonPath("$.data.content[0].month").value(6))
                .andExpect(jsonPath("$.data.content[0].periodStart").value("2025-06-01"))
                .andExpect(jsonPath("$.data.content[0].periodEnd").value("2025-06-30"))
                .andExpect(jsonPath("$.data.content[0].participantCount").value(32))
                .andDo(print());
    }
}
