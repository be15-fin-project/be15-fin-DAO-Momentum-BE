package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.dto.Status;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.service.HrObjectionQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(HrObjectionQueryController.class)
class HrObjectionQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HrObjectionQueryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("인사 평가 이의제기 목록 조회 성공")
    @WithMockUser(username = "1", roles = "MANAGER")
    void listObjections_success() throws Exception {
        // given
        HrObjectionItemDto item = HrObjectionItemDto.builder()
                .objectionId(5001L)
                .empNo("20240001")
                .employeeName("김현우")
                .roundNo(2)
                .createdAt("2025-06-15 14:23:45")
                .status(Status.PENDING)
                .score(85)
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1L)
                .build();

        HrObjectionListResultDto resultDto = new HrObjectionListResultDto(List.of(item), pagination);
        given(service.getObjections(any(HrObjectionListRequestDto.class))).willReturn(resultDto);

        // when & then
        mockMvc.perform(get("/hr-objections/requests")
                        .param("statusId", "1")
                        .param("roundNo", "2")
                        .param("startDate", "2025-06-01")
                        .param("endDate", "2025-06-30")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].objectionId").value(5001))
                .andExpect(jsonPath("$.data.list[0].empNo").value("20240001"))
                .andExpect(jsonPath("$.data.list[0].employeeName").value("김현우"))
                .andExpect(jsonPath("$.data.list[0].roundNo").value(2))
                .andExpect(jsonPath("$.data.list[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.list[0].score").value(85))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("이의제기 상세 조회 성공")
    @WithMockUser(username = "1", roles = "MANAGER")
    void getObjectionDetail_success() throws Exception {
        // given
        Long objectionId = 5001L;
        ObjectionItemDto itemDto = ObjectionItemDto.builder()
                .objectionId(objectionId)
                .resultId(100L)
                .empNo("20240001")
                .empName("김현우")
                .build();

        List<FactorScoreDto> factorScores = List.of(
                new FactorScoreDto("업무 수행 역량", "우수"),
                new FactorScoreDto("협업 역량", "보통")
        );

        WeightInfo weightInfo = new WeightInfo(20, 20, 20, 20, 10, 10);
        RateInfo rateInfo = new RateInfo(10, 20, 30, 30, 10);

        ObjectionDetailResultDto responseDto = ObjectionDetailResultDto.builder()
                .itemDto(itemDto)
                .factorScores(factorScores)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .build();

        given(service.getObjectionDetail(objectionId)).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/hr-objections/requests/{objectionId}", objectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.itemDto.objectionId").value(5001))
                .andExpect(jsonPath("$.data.factorScores", hasSize(2)))
                .andExpect(jsonPath("$.data.weightInfo.weightPerform").value(20))
                .andExpect(jsonPath("$.data.rateInfo.rateA").value(20))
                .andDo(print());
    }
}
