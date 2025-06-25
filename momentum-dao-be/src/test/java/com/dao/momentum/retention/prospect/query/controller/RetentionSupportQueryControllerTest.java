package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastItemDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionSupportDetailDto;
import com.dao.momentum.retention.prospect.query.service.RetentionSupportQueryService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(RetentionSupportQueryController.class)
class RetentionSupportQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetentionSupportQueryService retentionSupportQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("근속 전망 목록 조회 성공")
    @WithMockUser(authorities = "HR")
    void getRetentionForecasts_success() throws Exception {
        // given
        RetentionForecastItemDto item = RetentionForecastItemDto.builder()
                .empName("김예진")
                .deptName("인사팀")
                .positionName("과장")
                .retentionGrade("우수")
                .stabilityType(StabilityType.WARNING)
                .summaryComment("직무:우수, 관계:보통")
                .roundNo(3)
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        RetentionForecastResponseDto resultDto = RetentionForecastResponseDto.builder()
                .items(List.of(item))
                .pagination(pagination)
                .build();

        Mockito.when(retentionSupportQueryService.getRetentionForecasts(any(RetentionForecastRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/retention/forecast")
                        .param("roundNo", "3")
                        .param("deptId", "2")
                        .param("positionId", "4")
                        .param("stabilityType", "WARNING")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].empName").value("김예진"))
                .andExpect(jsonPath("$.data.items[0].stabilityType").value("주의형"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("근속 상세 정보 조회 성공")
    @WithMockUser(authorities = "HR")
    void getSupportDetail_success() throws Exception {
        // given
        Long retentionId = 1L;

        RetentionSupportDetailDto detailDto = RetentionSupportDetailDto.builder()
                .empName("박예린")
                .empNo("20250018")
                .deptName("개발팀")
                .positionName("사원")
                .retentionGrade("양호")
                .stabilityType(StabilityType.WARNING)
                .roundNo(2)
                .build();

        Mockito.when(retentionSupportQueryService.getSupportDetail(eq(retentionId)))
                .thenReturn(detailDto);

        // when & then
        mockMvc.perform(get("/retention/{retentionId}", retentionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.empName").value("박예린"))
                .andExpect(jsonPath("$.data.stabilityType").value("주의형"))
                .andDo(print());
    }
}
