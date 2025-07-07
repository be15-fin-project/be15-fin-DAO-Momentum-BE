package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormPromptRaw;
import com.dao.momentum.evaluation.eval.query.mapper.EvalFormMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvalFormQueryServiceImplTest {

    private EvalFormMapper evalFormMapper;
    private EvalFormQueryServiceImpl evalFormQueryService;

    @BeforeEach
    void setUp() {
        evalFormMapper = mock(EvalFormMapper.class);
        evalFormQueryService = new EvalFormQueryServiceImpl(evalFormMapper);
    }

    @Nested
    @DisplayName("평가 양식 상세 조회 (Get Form Detail)")
    class GetFormDetail {

        @Test
        @DisplayName("평가 양식 상세 조회 - 성공")
        void getFormDetail_success() {
            // given: Form prompts 데이터를 구성
            EvalFormPromptRaw raw1 = EvalFormPromptRaw.builder()
                    .formName("조직 몰입도")
                    .propertyId(101)  // propertyId가 다르므로 두 개의 FactorDto가 나와야 함
                    .propertyName("조직 몰입")
                    .promptId(301)
                    .content("회사의 비전에 공감하며 일하고 있다.")
                    .isPositive(true)
                    .build();
            EvalFormPromptRaw raw2 = EvalFormPromptRaw.builder()
                    .formName("조직 몰입도")
                    .propertyId(102)  // propertyId가 다르므로 두 개의 FactorDto가 나와야 함
                    .propertyName("조직 몰입")
                    .promptId(302)
                    .content("조직의 목표와 나의 목표가 일치한다.")
                    .isPositive(true)
                    .build();

            // mock the behavior of the mapper
            given(evalFormMapper.findFormDetailByFormId(1)).willReturn(List.of(raw1, raw2));

            // when: 서비스 호출
            EvalFormDetailResultDto result = evalFormQueryService.getFormDetail(1, null);

            // then: 반환된 값 검증
            assertThat(result.formName()).isEqualTo("조직 몰입도");
            assertThat(result.factors()).hasSize(2);  // 이제 두 개의 factor가 반환되어야 함
            assertThat(result.factors().get(0).propertyName()).isEqualTo("조직 몰입");
            assertThat(result.factors().get(0).prompts()).hasSize(1);  // 1개의 prompt가 있어야 함
            assertThat(result.factors().get(0).prompts().get(0).content()).isEqualTo("회사의 비전에 공감하며 일하고 있다.");

            assertThat(result.factors().get(1).propertyName()).isEqualTo("조직 몰입");
            assertThat(result.factors().get(1).prompts()).hasSize(1);  // 1개의 prompt가 있어야 함
            assertThat(result.factors().get(1).prompts().get(0).content()).isEqualTo("조직의 목표와 나의 목표가 일치한다.");

            then(evalFormMapper).should().findFormDetailByFormId(1);  // Verify the mock interaction
        }

        @Test
        @DisplayName("평가 양식 상세 조회 - 문항 없음으로 예외 발생")
        void getFormDetail_notFound() {
            // given: 없는 formId에 대해 빈 리스트 반환
            given(evalFormMapper.findFormDetailByFormId(999)).willReturn(List.of());  // Mock empty response for non-existent form

            // when & then: 예외가 발생해야 함
            assertThatThrownBy(() -> evalFormQueryService.getFormDetail(999, null))
                    .isInstanceOf(EvalException.class)  // Expect EvalException
                    .hasMessageContaining("해당 양식의 문항 정보를 찾을 수 없습니다");
        }
    }
}
