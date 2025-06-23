package com.dao.momentum.retention.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactItemDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactListResultDto;
import com.dao.momentum.retention.query.service.RetentionContactQueryService;
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

@WebMvcTest(RetentionContactQueryController.class)
class RetentionContactQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetentionContactQueryService contactQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("면담 기록 전체 조회 성공")
    @WithMockUser(authorities = "HR_MANAGER")
    void getContactList_success() throws Exception {
        // given
        RetentionContactItemDto item = RetentionContactItemDto.builder()
                .createdAt(LocalDateTime.of(2025, 6, 23, 15, 28, 18))
                .empNo("20250020")
                .targetName("이하준")
                .deptName("프론트엔드팀")
                .positionName("대리")
                .managerName("정지우")
                .reason("복지 불만 비율이 높아짐")
                .build();

        RetentionContactListResultDto resultDto = RetentionContactListResultDto.builder()
                .items(List.of(item))
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalItems(1L)
                        .totalPage(1)
                        .build())
                .build();

        Mockito.when(contactQueryService.getContactList(any(RetentionContactListRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/retention/contact")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].empNo").value("20250020"))
                .andExpect(jsonPath("$.data.items[0].targetName").value("이하준"))
                .andExpect(jsonPath("$.data.items[0].deptName").value("프론트엔드팀"))
                .andExpect(jsonPath("$.data.items[0].positionName").value("대리"))
                .andExpect(jsonPath("$.data.items[0].managerName").value("정지우"))
                .andExpect(jsonPath("$.data.items[0].reason").value("복지 불만 비율이 높아짐"))
                .andDo(print());
    }

    @Test
    @DisplayName("나에게 요청된 면담 기록 조회 성공")
    @WithMockUser(username = "34", authorities = "MANAGER")
    void getMyContactRequests_success() throws Exception {
        // given
        RetentionContactItemDto item = RetentionContactItemDto.builder()
                .createdAt(LocalDateTime.of(2025, 6, 23, 15, 28, 18))
                .empNo("20250020")
                .targetName("이하준")
                .deptName("프론트엔드팀")
                .positionName("대리")
                .managerName("정지우")
                .reason("복지 불만 비율이 높아짐")
                .build();

        RetentionContactListResultDto resultDto = RetentionContactListResultDto.builder()
                .items(List.of(item))
                .pagination(Pagination.builder()
                        .currentPage(1)
                        .totalItems(1L)
                        .totalPage(1)
                        .build())
                .build();

        Mockito.when(contactQueryService.getMyRequestedContactList(anyLong(), any(RetentionContactListRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/retention/contact/my")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].empNo").value("20250020"))
                .andExpect(jsonPath("$.data.items[0].targetName").value("이하준"))
                .andExpect(jsonPath("$.data.items[0].deptName").value("프론트엔드팀"))
                .andExpect(jsonPath("$.data.items[0].positionName").value("대리"))
                .andExpect(jsonPath("$.data.items[0].managerName").value("정지우"))
                .andExpect(jsonPath("$.data.items[0].reason").value("복지 불만 비율이 높아짐"))
                .andDo(print());
    }
}
