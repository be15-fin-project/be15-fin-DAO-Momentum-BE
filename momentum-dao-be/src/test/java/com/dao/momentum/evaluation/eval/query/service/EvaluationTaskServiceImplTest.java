package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
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

        // mapper stubbing
        given(mapper.findLatestRoundNo()).willReturn(latestRoundNo);

        EvaluatorRoleDto evaluator = EvaluatorRoleDto.builder()
                .isTeamLeader(false)
                .isDeptHead(false)
                .build();
        given(mapper.findEvaluatorRole(empId)).willReturn(evaluator);

        // 계산된 offset = (page-1)*size = 0
        int size = 5;
        int offset = 0;

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

        given(mapper.findTasks(eq(req), eq(empId), eq(latestRoundNo), eq(evaluator), eq(size), eq(offset)))
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

        // verify 호출 인자
        verify(mapper).findLatestRoundNo();
        verify(mapper).findEvaluatorRole(empId);
        verify(mapper).findTasks(req, empId, latestRoundNo, evaluator, size, offset);
        verify(mapper).countTasks(req, empId, latestRoundNo, evaluator);

        verifyNoMoreInteractions(mapper);
    }


    @Test
    @DisplayName("미제출자 목록 조회 - 성공")
    void getNoneSubmitters_success() {
        // given
        int roundId = 3;

        EmployeeSimpleDto emp1 = EmployeeSimpleDto.builder()
                .empId(100L)
                .empNo("20250001")
                .name("김사원")
                .deptId(10L)
                .deptName("영업팀")
                .build();

        EmployeeSimpleDto emp2 = EmployeeSimpleDto.builder()
                .empId(101L)
                .empNo("20250002")
                .name("이철수")
                .deptId(10L)
                .deptName("영업팀")
                .build();

        List<EmployeeSimpleDto> employees = List.of(emp1, emp2);
        given(mapper.findAllActiveEmployees()).willReturn(employees);

        EvaluatorRoleDto evaluator = EvaluatorRoleDto.builder()
                .isDeptHead(false)
                .isTeamLeader(false)
                .build();
        given(mapper.findEvaluatorRole(anyLong())).willReturn(evaluator);

        // emp1은 제출 안 함
        EvaluationTaskResponseDto task1 = EvaluationTaskResponseDto.builder()
                .formId(1)
                .submitted(false)
                .build();

        // emp2는 다 제출함
        EvaluationTaskResponseDto task2 = EvaluationTaskResponseDto.builder()
                .formId(1)
                .submitted(true)
                .build();

        given(mapper.findAllTasks(any(), eq(100L), eq(roundId), eq(evaluator), anyInt(), eq(0)))
                .willReturn(List.of(task1));
        given(mapper.findAllTasks(any(), eq(101L), eq(roundId), eq(evaluator), anyInt(), eq(0)))
                .willReturn(List.of(task2));

        // when
        List<NoneSubmitDto> result = service.getNoneSubmitters(roundId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpNo()).isEqualTo("20250001");

        verify(mapper).findAllActiveEmployees();
        verify(mapper, times(2)).findEvaluatorRole(anyLong());
        verify(mapper).findAllTasks(any(), eq(100L), eq(roundId), eq(evaluator), anyInt(), eq(0));
        verify(mapper).findAllTasks(any(), eq(101L), eq(roundId), eq(evaluator), anyInt(), eq(0));
    }
}
