package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormPromptRaw;
import com.dao.momentum.evaluation.eval.query.mapper.EvalFormMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("평가 양식 상세 조회 - 성공")
    void getFormDetail_success() {
        // given
        EvalFormPromptRaw raw1 = new EvalFormPromptRaw();
        EvalFormPromptRaw raw2 = new EvalFormPromptRaw();

        // 공통 필드
        setField(raw1, "formName", "조직 몰입도");
        setField(raw2, "formName", "조직 몰입도");

        // 요인 정보
        setField(raw1, "propertyId", 1001);
        setField(raw1, "propertyName", "조직 헌신");
        setField(raw1, "promptId", 1);
        setField(raw1, "content", "나는 이 조직의 목표에 동의한다.");
        setField(raw1, "isPositive", true);

        setField(raw2, "propertyId", 1002);
        setField(raw2, "propertyName", "몰입 수준");
        setField(raw2, "promptId", 2);
        setField(raw2, "content", "업무에 적극적으로 참여한다.");
        setField(raw2, "isPositive", true);

        given(evalFormMapper.findFormDetailByFormId(1)).willReturn(List.of(raw1, raw2));

        // when
        EvalFormDetailResultDto result = evalFormQueryService.getFormDetail(1, null);

        // then
        assertThat(result.getFormName()).isEqualTo("조직 몰입도");
        assertThat(result.getFactors()).hasSize(2);
        assertThat(result.getFactors().get(0).getPrompts().get(0).getContent()).isEqualTo("나는 이 조직의 목표에 동의한다.");
        assertThat(result.getFactors().get(1).getPrompts().get(0).getContent()).isEqualTo("업무에 적극적으로 참여한다.");

        then(evalFormMapper).should().findFormDetailByFormId(1);
    }

    @Test
    @DisplayName("평가 양식 상세 조회 - 문항 없음으로 예외 발생")
    void getFormDetail_notFound() {
        // given
        given(evalFormMapper.findFormDetailByFormId(999)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> evalFormQueryService.getFormDetail(999, null))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_PROMPT_NOT_FOUND.getMessage());
    }

    // 유틸: private 필드 세팅
    private void setField(Object target, String name, Object value) {
        org.springframework.test.util.ReflectionTestUtils.setField(target, name, value);
    }
}
