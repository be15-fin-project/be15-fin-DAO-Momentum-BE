package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.service.MyObjectionQueryService;
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
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2025-06-22 17:31:08"))
                .andExpect(jsonPath("$.data.content[0].overallGrade").value("A"))
                .andExpect(jsonPath("$.data.content[0].statusType").value("PENDING"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }
}
