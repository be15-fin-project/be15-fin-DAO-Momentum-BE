package com.dao.momentum.evaluation.eval.query.service;

import java.util.ArrayList;
import java.util.List;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.EmployeeSimpleDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluatorRoleDto;
import com.dao.momentum.evaluation.eval.query.dto.request.NoneSubmitDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationTaskMapper;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationTaskServiceImpl implements EvaluationTaskService {

    private final EvaluationTaskMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public EvaluationTaskListResultDto getTasks(Long empId, EvaluationTaskRequestDto req) {
        log.info("[EvaluationTaskServiceImpl] getTasks() 호출 시작 - empId={}, req={}", empId, req);

        // roundNo가 0이면 최신 회차로 자동 설정
        if (req.roundId() == null || req.roundId() == 0) {
            int latest = mapper.findLatestRoundId();
            if (latest == 0) {  // 최신 라운드가 없을 경우
                throw new EvalException(ErrorCode.EVAL_ROUND_NOT_FOUND);
            }
            req = EvaluationTaskRequestDto.builder()
                    .roundId(latest)         // 최신 roundNo로 갱신
                    .page(req.page())        // 기존 page 유지
                    .size(req.size())        // 기존 size 유지
                    .build();
            log.info("자동 설정된 최신 roundNo={}", latest);
        }

        // evaluator 역할 조회
        EvaluatorRoleDto evaluator = mapper.findEvaluatorRole(empId);
        log.info("조회된 evaluator 역할: {}", evaluator);

        // offset 계산 (getOffset 메서드 사용)
        int offset = req.getOffset(); // getOffset 메서드를 사용하여 계산된 offset
        int roundNo = mapper.findRoundNoByRoundId(req.roundId());
        int size = req.size();

        // 평가 태스크 조회
        List<EvaluationTaskResponseDto> tasks = mapper.findTasks(req, empId, roundNo, evaluator, size, offset);
        int total = mapper.countTasks(req, empId, roundNo, evaluator);

        log.info("평가 태스크 조회 완료 - tasks.size={}, total={}", tasks.size(), total);

        // Pagination 계산
        Pagination pagination = buildPagination(req.page(), size, total);
        EvaluationTaskListResultDto result = EvaluationTaskListResultDto.builder()
                .tasks(tasks)
                .pagination(pagination)
                .build();

        log.info("최종 반환 결과: {}", result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoneSubmitDto> getNoneSubmitters(Integer roundId) {
        log.info("[EvaluationTaskServiceImpl] getNoneSubmitters() 호출 시작 - roundId={}", roundId);

        List<EmployeeSimpleDto> allEmployees = mapper.findAllActiveEmployees();
        List<NoneSubmitDto> result = new ArrayList<>();

        log.info("전체 검사 대상 사원 수: {}", allEmployees.size());

        for (EmployeeSimpleDto emp : allEmployees) {
            EvaluatorRoleDto evaluator = mapper.findEvaluatorRole(emp.empId());

            // 요청 DTO 생성
            EvaluationTaskRequestDto req = EvaluationTaskRequestDto.builder()
                    .formId(0)        // formId를 0으로 설정 (모든 양식 포함)
                    .roundId(roundId)  // 해당 회차로 설정
                    .page(1)           // 기본값 페이지 설정 (기본값을 사용)
                    .size(Integer.MAX_VALUE)  // 모든 결과를 조회 (최대 크기)
                    .build();

            // 해당 사원의 평가 태스크 조회
            List<EvaluationTaskResponseDto> tasks = mapper.findAllTasks(
                    req,
                    emp.empId(),
                    roundId,
                    evaluator,
                    Integer.MAX_VALUE,
                    0
            );

            // 제출되지 않은 태스크가 있는지 체크
            boolean hasUnsubmitted = tasks.stream().anyMatch(t -> !t.submitted());

            if (hasUnsubmitted) {
                log.info("미제출자 발견 - name={}, taskCount={}", emp.name(), tasks.size());

                result.add(NoneSubmitDto.builder()
                        .empId(emp.empId())
                        .empNo(emp.empNo())
                        .name(emp.name())
                        .deptId(emp.deptId())
                        .deptName(emp.deptName())
                        .build());
            }
        }

        log.info("미제출자 총 인원 수: {}", result.size());
        return result;
    }

    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
