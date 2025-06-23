package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskResponseDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EvaluationTaskServiceImplTest {

    private EvaluationTaskServiceImpl service;
    private EvaluationTaskMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = mock(EvaluationTaskMapper.class);
        service = new EvaluationTaskServiceImpl(mapper);
    }

    @Test
    @DisplayName("평가 제출 목록 조회 - 성공")
    void getTasks_useLatestRound_success() {
        // given
        long empId = 53L;
        EvaluationTaskRequestDto req = new EvaluationTaskRequestDto();
        // formId, page, size 기본값: 0,1,20
        ReflectionTestUtils.setField(req, "page", 1);
        ReflectionTestUtils.setField(req, "size", 5);

        // 최신 라운드 번호 모의
        given(mapper.findLatestRoundNo()).willReturn(7);

        // findTasks/countTasks 모의
        EvaluationTaskResponseDto dto = EvaluationTaskResponseDto.builder()
                .roundNo(7)
                .typeName("SELF")
                .formId(3)
                .deptId(10)
                .targetEmpNo(null)
                .targetName("홍길동")
                .submitted(true)
                .startAt(LocalDate.of(2025,6,1))
                .build();
        given(mapper.findTasks(eq(req), eq(empId), eq(7))).willReturn(List.of(dto));
        given(mapper.countTasks(eq(req), eq(empId), eq(7))).willReturn(1);

        // when
        EvaluationTaskListResultDto result = service.getTasks(empId, req);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTasks()).hasSize(1)
                .first()
                .extracting(EvaluationTaskResponseDto::getRoundNo,
                        EvaluationTaskResponseDto::getTypeName,
                        EvaluationTaskResponseDto::isSubmitted)
                .containsExactly(7, "SELF", true);

        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isEqualTo(1);
        assertThat(p.getTotalPage()).isEqualTo(1);

        // verify 최신 라운드 조회 및 매퍼 호출
        verify(mapper).findLatestRoundNo();
        verify(mapper).findTasks(req, empId, 7);
        verify(mapper).countTasks(req, empId, 7);
    }

}
