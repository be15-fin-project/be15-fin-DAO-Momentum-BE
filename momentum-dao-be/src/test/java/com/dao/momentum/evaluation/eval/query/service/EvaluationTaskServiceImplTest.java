package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluatorRoleDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("평가 제출 목록 조회 - 성공 (최신 라운드 자동 설정)")
    void getTasks_useLatestRound_success() {
        // given
        long empId = 53L;
        EvaluationTaskRequestDto req = new EvaluationTaskRequestDto();
        ReflectionTestUtils.setField(req, "page", 1);
        ReflectionTestUtils.setField(req, "size", 5);
        ReflectionTestUtils.setField(req, "roundNo", 0); // 최신 라운드 사용 조건

        int latestRoundNo = 7;

        given(mapper.findLatestRoundNo()).willReturn(latestRoundNo);

        EvaluatorRoleDto evaluator = EvaluatorRoleDto.builder()
                .isTeamLeader(false)
                .isDeptHead(false)
                .build();
        given(mapper.findEvaluatorRole(empId)).willReturn(evaluator);

        EvaluationTaskResponseDto dto = EvaluationTaskResponseDto.builder()
                .roundNo(latestRoundNo)
                .typeName("SELF")
                .formId(3)
                .deptId(10)
                .targetEmpNo("20250001")
                .targetName("홍길동")
                .submitted(true)
                .startAt(LocalDate.of(2025, 6, 1))
                .build();

        given(mapper.findTasks(eq(req), eq(empId), eq(latestRoundNo), eq(evaluator)))
                .willReturn(List.of(dto));
        given(mapper.countTasks(eq(req), eq(empId), eq(latestRoundNo), eq(evaluator)))
                .willReturn(1);

        // when
        EvaluationTaskListResultDto result = service.getTasks(empId, req);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTasks()).hasSize(1)
                .first()
                .extracting(EvaluationTaskResponseDto::getRoundNo,
                        EvaluationTaskResponseDto::getTypeName,
                        EvaluationTaskResponseDto::isSubmitted)
                .containsExactly(latestRoundNo, "SELF", true);

        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isEqualTo(1);
        assertThat(p.getTotalPage()).isEqualTo(1);

        verify(mapper).findLatestRoundNo();
        verify(mapper).findEvaluatorRole(empId);
        verify(mapper).findTasks(req, empId, latestRoundNo, evaluator);
        verify(mapper).countTasks(req, empId, latestRoundNo, evaluator);
    }
}
