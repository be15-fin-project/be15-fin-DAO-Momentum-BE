package com.dao.momentum.evaluation.eval.query.service;

import java.util.ArrayList;
import java.util.List;

import com.dao.momentum.common.dto.Pagination;
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

        if (req.getRoundNo() == 0) {
            int latest = mapper.findLatestRoundNo();
            req.setRoundNo(latest);
            log.info("자동 설정된 최신 roundNo={}", latest);
        }

        EvaluatorRoleDto evaluator = mapper.findEvaluatorRole(empId);
        log.info("조회된 evaluator 역할: {}", evaluator);

        int roundNo = req.getRoundNo();
        int offset = req.getOffset();
        int size = req.getSize();

        List<EvaluationTaskResponseDto> tasks = mapper.findTasks(req, empId, roundNo, evaluator, size, offset);
        int total = mapper.countTasks(req, empId, roundNo, evaluator);

        log.info("평가 태스크 조회 완료 - tasks.size={}, total={}", tasks.size(), total);

        Pagination pagination = buildPagination(req.getPage(), size, total);
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
            EvaluatorRoleDto evaluator = mapper.findEvaluatorRole(emp.getEmpId());

            EvaluationTaskRequestDto req = new EvaluationTaskRequestDto();
            req.setRoundNo(roundId);
            req.setFormId(0); // 전체 폼

            List<EvaluationTaskResponseDto> tasks = mapper.findAllTasks(
                    req,
                    emp.getEmpId(),
                    roundId,
                    evaluator,
                    Integer.MAX_VALUE,
                    0
            );

            boolean hasUnsubmitted = tasks.stream().anyMatch(t -> !t.isSubmitted());

            if (hasUnsubmitted) {
                log.info("미제출자 발견 - name={}, taskCount={}", emp.getName(), tasks.size());

                result.add(NoneSubmitDto.builder()
                        .empId(emp.getEmpId())
                        .empNo(emp.getEmpNo())
                        .name(emp.getName())
                        .deptId(emp.getDeptId())
                        .deptName(emp.getDeptName())
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
