package com.dao.momentum.evaluation.eval.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto.FactorDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto.PromptDto;
import com.dao.momentum.evaluation.eval.query.service.EvalFormQueryService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(EvalFormQueryController.class)
class EvalFormQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvalFormQueryService evalFormQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("평가 양식 상세 조회 성공")
    @WithMockUser
    void getFormDetail_success() throws Exception {
        // given: EvalFormDetailResultDto 생성
        PromptDto prompt1 = PromptDto.builder()
                .content("회사의 비전에 공감하며 일하고 있다.")
                .isPositive(true)  // 필드 확인
                .build();

        PromptDto prompt2 = PromptDto.builder()
                .content("조직의 목표와 나의 목표가 일치한다.")
                .isPositive(true)  // 필드 확인
                .build();

        FactorDto factor = FactorDto.builder()
                .propertyName("조직 몰입")
                .prompts(List.of(prompt1, prompt2))
                .build();

        EvalFormDetailResultDto responseDto = EvalFormDetailResultDto.builder()
                .formName("조직 몰입도")
                .factors(List.of(factor))
                .build();

        // mock service method
        Mockito.when(evalFormQueryService.getFormDetail(anyInt(), anyInt()))
                .thenReturn(responseDto);

        // when & then: 실제 API 호출을 수행하고 응답 결과를 검증
        mockMvc.perform(get("/eval-forms/1")
                        .param("roundId", "3"))
                .andExpect(status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
                .andExpect(jsonPath("$.success").value(true))  // success 필드 값이 true인지 확인
                .andExpect(jsonPath("$.data.formName").value("조직 몰입도"))  // formName이 올바른지 확인
                .andExpect(jsonPath("$.data.factors", hasSize(1)))  // factors 배열의 크기가 1인지 확인
                .andExpect(jsonPath("$.data.factors[0].propertyName").value("조직 몰입"))  // factor의 propertyName 확인
                .andExpect(jsonPath("$.data.factors[0].prompts", hasSize(2)))  // prompts가 2개인지 확인
                .andExpect(jsonPath("$.data.factors[0].prompts[0].content").value("회사의 비전에 공감하며 일하고 있다."))  // 첫 번째 prompt의 content 확인
                .andExpect(jsonPath("$.data.factors[0].prompts[0].isPositive").value(true))  // 첫 번째 prompt의 positive 값 확인
                .andDo(print());  // 응답 내용 출력 (디버깅 용도)
    }

}
