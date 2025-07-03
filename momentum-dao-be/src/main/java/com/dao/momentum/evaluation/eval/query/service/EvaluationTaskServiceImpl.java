package com.dao.momentum.evaluation.eval.query.service;

import java.util.ArrayList;
import java.util.List;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationTaskMapper;
import com.dao.momentum.evaluation.eval.query.service.EvaluationTaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluationTaskServiceImpl implements EvaluationTaskService {

    private final EvaluationTaskMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public EvaluationTaskListResultDto getTasks(Long empId, EvaluationTaskRequestDto req) {
        // roundNo 기본 처리: 0 이면 최신 라운드 번호로 채워주기
        if (req.getRoundNo() == 0) {
            int latest = mapper.findLatestRoundNo();
            req.setRoundNo(latest);
        }

        EvaluatorRoleDto evaluator = mapper.findEvaluatorRole(empId);
        System.out.println("[EvaluationTaskService] evaluator 역할 정보 = " + evaluator);

        // [LOG] 요청 파라미터
        System.out.println(String.format(
                "[EvaluationTaskService] empId=%s, formId=%d, roundNo=%d, page=%d, size=%d",
                empId, req.getFormId(), req.getRoundNo(), req.getPage(), req.getSize()
        ));

        // 1) DMP ID(emp_no) → 내부 empId 변환
        System.out.println("[EvaluationTaskService] empId 조회 결과=" + empId);

        // 2) 요청 DTO에서 formId, roundNo, page, size 추출
        int typeId = req.getTypeId();
        int formId = req.getFormId();
        int roundNo = req.getRoundNo();
        int offset = req.getOffset();
        int size = req.getSize();
        System.out.println(String.format(
                "[EvaluationTaskService] offset=%d, size=%d, typeId=%d, formId=%d, roundNo=%d",
                offset, size, typeId, formId, roundNo
        ));

        // 3) 평가 태스크 목록 조회 (SELF, ORG, PEER 통합)
        List<EvaluationTaskResponseDto> tasks = mapper.findTasks(req, empId, roundNo, evaluator, size, offset);
        System.out.println("[EvaluationTaskService] findTasks 결과 개수=" + tasks.size());

        // 4) 전체 건수 조회 (페이징 용)
        int total = mapper.countTasks(req, empId, roundNo, evaluator);
        System.out.println("[EvaluationTaskService] countTasks 결과 total=" + total);

        // 5) Pagination 생성 및 결과 포장
        Pagination pagination = buildPagination(req.getPage(), size, total);
        EvaluationTaskListResultDto result = EvaluationTaskListResultDto.builder()
                .tasks(tasks)
                .pagination(pagination)
                .build();
        System.out.println("[EvaluationTaskService] 최종 반환 DTO=" + result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoneSubmitDto> getNoneSubmitters(Integer roundId) {

        List<EmployeeSimpleDto> allEmployees = mapper.findAllActiveEmployees();
        List<NoneSubmitDto> result = new ArrayList<>();
        System.out.println("▶️ 검사 대상 사원 수 = " + allEmployees.size());

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
                    Integer.MAX_VALUE, // 모든 태스크 가져오기
                    0
            );

            System.out.println("🔍 " + emp.getName() + " → task 개수 = " + tasks.size());

            for (EvaluationTaskResponseDto task : tasks) {
                System.out.println("   ▫️ formId=" + task.getFormId() + ", submitted=" + task.isSubmitted());
            }
            boolean hasUnsubmitted = tasks.stream().anyMatch(t -> !t.isSubmitted());

            if (hasUnsubmitted) {
                System.out.println("❌ 미제출자 발견: " + emp.getName());
                result.add(NoneSubmitDto.builder()
                        .empId(emp.getEmpId())
                        .empNo(emp.getEmpNo())
                        .name(emp.getName())
                        .deptId(emp.getDeptId())
                        .deptName(emp.getDeptName())
                        .build());
            }
        }

        return result;
    }

    /**
     * 페이지네이션 객체 생성
     */
    private Pagination buildPagination(int page, int size, long total) {
        int totalPage = (int) Math.ceil((double) total / size);
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}