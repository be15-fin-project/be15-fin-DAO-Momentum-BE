package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskResponseDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationTaskService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationTaskController.class)
class EvaluationTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationTaskService evaluationTaskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("내 평가 태스크 조회 성공 (전체 폼)")
    @WithMockUser(username = "53")
    void getMyTasks_allForms_success() throws Exception {
        // given
        EvaluationTaskResponseDto self = EvaluationTaskResponseDto.builder()
                .roundNo(2)
                .typeName("SELF")
                .formId(8)
                .deptId(10)
                .targetEmpNo(null)
                .targetName("홍길동")
                .submitted(false)
                .startAt(LocalDate.of(2025,6,1))
                .build();
        EvaluationTaskResponseDto org = EvaluationTaskResponseDto.builder()
                .roundNo(2)
                .typeName("ORG")
                .formId(5)
                .deptId(10)
                .targetEmpNo(null)
                .targetName("홍길동")
                .submitted(true)
                .startAt(LocalDate.of(2025,6,1))
                .build();
        EvaluationTaskResponseDto peer = EvaluationTaskResponseDto.builder()
                .roundNo(2)
                .typeName("PEER_REVIEW")
                .formId(1)
                .deptId(10)
                .targetEmpNo(20250002L)
                .targetName("김철수")
                .submitted(false)
                .startAt(LocalDate.of(2025,6,1))
                .build();

        List<EvaluationTaskResponseDto> list = List.of(self, org, peer);
        Pagination pag = Pagination.builder().currentPage(1).totalPage(1).totalItems(3).build();
        EvaluationTaskListResultDto result = EvaluationTaskListResultDto.builder()
                .tasks(list)
                .pagination(pag)
                .build();

        Mockito.when(evaluationTaskService.getTasks(anyLong(), any(EvaluationTaskRequestDto.class)))
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/evaluation/tasks")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tasks", hasSize(3)))
                .andExpect(jsonPath("$.data.tasks[0].typeName").value("SELF"))
                .andExpect(jsonPath("$.data.tasks[1].typeName").value("ORG"))
                .andExpect(jsonPath("$.data.tasks[2].targetName").value("김철수"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(3))
                .andReturn();
    }

    @Test
    @DisplayName("내 평가 태스크 조회 성공 (특정 formId)")
    @WithMockUser(username = "53")
    void getMyTasks_filterByForm_success() throws Exception {
        // given
        EvaluationTaskResponseDto upward = EvaluationTaskResponseDto.builder()
                .roundNo(2)
                .typeName("UPWARD_REVIEW")
                .formId(2)
                .deptId(10)
                .targetEmpNo(20250003L)
                .targetName("이영희")
                .submitted(true)
                .startAt(LocalDate.of(2025,6,1))
                .build();

        List<EvaluationTaskResponseDto> list = List.of(upward);
        Pagination pag = Pagination.builder().currentPage(1).totalPage(1).totalItems(1).build();
        EvaluationTaskListResultDto result = EvaluationTaskListResultDto.builder()
                .tasks(list)
                .pagination(pag)
                .build();

        Mockito.when(evaluationTaskService.getTasks(eq(53L), argThat(req -> req.getFormId() == 2)))
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/evaluation/tasks")
                        .param("formId", "2")
                        .param("roundNo", "2")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tasks", hasSize(1)))
                .andExpect(jsonPath("$.data.tasks[0].typeName").value("UPWARD_REVIEW"))
                .andExpect(jsonPath("$.data.tasks[0].targetName").value("이영희"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andReturn();
    }
}
