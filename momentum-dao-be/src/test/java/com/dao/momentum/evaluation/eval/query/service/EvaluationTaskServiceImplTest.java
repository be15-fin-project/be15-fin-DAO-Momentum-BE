package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.request.EmployeeSimpleDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluatorRoleDto;
import com.dao.momentum.evaluation.eval.query.dto.request.NoneSubmitDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Nested
    @DisplayName("평가 제출 목록 조회 (Get Evaluation Tasks)")
    class GetEvaluationTasks {

        @Test
        @DisplayName("평가 제출 목록 조회 - 성공 (최신 라운드 자동 설정)")
        void getTasks_latestRoundAutoAssigned_success() {
            // given
            long empId = 53L;
            int latestRoundId = 3;
            int latestRoundNo = 7;

            EvaluationTaskRequestDto req = EvaluationTaskRequestDto.builder()
                    .roundId(null)  // null로 최신 회차 요청
                    .page(1)
                    .size(10)
                    .build();

            EvaluatorRoleDto evaluator = EvaluatorRoleDto.builder()
                    .empId(empId)
                    .deptId(10L)
                    .isDeptHead(false)
                    .isTeamLeader(false)
                    .build();

            given(mapper.findLatestRoundId()).willReturn(latestRoundId);
            given(mapper.findRoundNoByRoundId(latestRoundId)).willReturn(latestRoundNo);
            given(mapper.findEvaluatorRole(empId)).willReturn(evaluator);

            EvaluationTaskResponseDto task = EvaluationTaskResponseDto.builder()
                    .roundNo(latestRoundNo)
                    .typeName("SELF")
                    .formId(3)
                    .deptId(10)
                    .targetEmpNo("20250001")
                    .targetName("홍길동")
                    .submitted(true)
                    .startAt(LocalDate.of(2025, 6, 1))
                    .build();

            given(mapper.findTasks(any(), eq(empId), eq(latestRoundNo), eq(evaluator), eq(10), eq(0)))
                    .willReturn(List.of(task));
            given(mapper.countTasks(any(), eq(empId), eq(latestRoundNo), eq(evaluator)))
                    .willReturn(1);

            // when
            EvaluationTaskListResultDto result = service.getTasks(empId, req);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tasks()).hasSize(1);
            assertThat(result.tasks().get(0).roundNo()).isEqualTo(latestRoundNo);
            assertThat(result.tasks().get(0).submitted()).isTrue();
            assertThat(result.pagination().getTotalItems()).isEqualTo(1);
            assertThat(result.pagination().getTotalPage()).isEqualTo(1);

            verify(mapper).findLatestRoundId();
            verify(mapper).findRoundNoByRoundId(latestRoundId);
            verify(mapper).findEvaluatorRole(empId);
        }

    }

    @Nested
    @DisplayName("미제출자 목록 조회 (Get None Submitters)")
    class GetNoneSubmitters {

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
            assertThat(result.get(0).empNo()).isEqualTo("20250001");

            verify(mapper).findAllActiveEmployees();
            verify(mapper, times(2)).findEvaluatorRole(anyLong());
            verify(mapper).findAllTasks(any(), eq(100L), eq(roundId), eq(evaluator), anyInt(), eq(0));
            verify(mapper).findAllTasks(any(), eq(101L), eq(roundId), eq(evaluator), anyInt(), eq(0));
        }
    }

    @Nested
    @DisplayName("기타 평가 관련 테스트 (Other Evaluation Tests)")
    class OtherEvaluationTests {

        @Test
        @DisplayName("미제출자 목록 조회 - 결과 없음")
        void getNoneSubmitters_empty() {
            // given
            int roundId = 3;

            EmployeeSimpleDto emp1 = EmployeeSimpleDto.builder()
                    .empId(100L)
                    .empNo("20250001")
                    .name("김사원")
                    .deptId(10L)
                    .deptName("영업팀")
                    .build();

            List<EmployeeSimpleDto> employees = List.of(emp1);
            given(mapper.findAllActiveEmployees()).willReturn(employees);

            EvaluatorRoleDto evaluator = EvaluatorRoleDto.builder()
                    .isDeptHead(false)
                    .isTeamLeader(false)
                    .build();
            given(mapper.findEvaluatorRole(anyLong())).willReturn(evaluator);

            // emp1은 다 제출함
            EvaluationTaskResponseDto task1 = EvaluationTaskResponseDto.builder()
                    .formId(1)
                    .submitted(true)
                    .build();

            given(mapper.findAllTasks(any(), eq(100L), eq(roundId), eq(evaluator), anyInt(), eq(0)))
                    .willReturn(List.of(task1));

            // when
            List<NoneSubmitDto> result = service.getNoneSubmitters(roundId);

            // then
            assertThat(result).isEmpty();

            verify(mapper).findAllActiveEmployees();
            verify(mapper).findEvaluatorRole(anyLong());
            verify(mapper).findAllTasks(any(), eq(100L), eq(roundId), eq(evaluator), anyInt(), eq(0));
        }
    }
}
