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
        void getTasks_useFixedRound_success() {
            // given
            long empId = 53L;
            int fixedRoundNo = 7;  // 고정된 roundNo로 7 설정
            EvaluationTaskRequestDto req = EvaluationTaskRequestDto.builder()
                    .roundId(fixedRoundNo)  // 고정된 roundNo 사용
                    .page(1)
                    .size(10)
                    .build();

            // 평가자 역할 설정
            given(mapper.findEvaluatorRole(empId)).willReturn(
                    EvaluatorRoleDto.builder()
                            .empId(empId)
                            .deptId(10L)
                            .isDeptHead(false)
                            .isTeamLeader(false)
                            .build()
            );

            // Mock된 데이터 반환
            EvaluationTaskResponseDto dto = EvaluationTaskResponseDto.builder()
                    .roundNo(fixedRoundNo)  // 고정된 roundNo
                    .typeName("SELF")
                    .formId(3)
                    .deptId(10)
                    .targetEmpNo("20250001")
                    .targetName("홍길동")
                    .submitted(true)  // 평가 완료된 태스크
                    .startAt(LocalDate.of(2025, 6, 1))
                    .build();

            // findTasks에서 반환할 데이터를 Mock 설정
            given(mapper.findTasks(eq(req), eq(empId), eq(fixedRoundNo), any(), eq(10), eq(0)))
                    .willReturn(List.of(dto));  // 1개 항목 반환
            given(mapper.countTasks(eq(req), eq(empId), eq(fixedRoundNo), any()))
                    .willReturn(1);

            // when
            EvaluationTaskListResultDto result = service.getTasks(empId, req);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tasks()).hasSize(1)
                    .first()
                    .extracting(EvaluationTaskResponseDto::roundNo,
                            EvaluationTaskResponseDto::typeName,
                            EvaluationTaskResponseDto::submitted)
                    .containsExactly(fixedRoundNo, "SELF", true);

            assertThat(result.pagination().getCurrentPage()).isEqualTo(1);
            assertThat(result.pagination().getTotalItems()).isEqualTo(1);
            assertThat(result.pagination().getTotalPage()).isEqualTo(1);
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
