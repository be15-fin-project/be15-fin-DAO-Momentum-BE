package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.query.service.ManagerFinderService;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.interview.command.application.service.RetentionContactCommandService;
import com.dao.momentum.retention.prospect.command.application.service.RetentionSupportCommandService;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionSupportRepository;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionSupportCommandServiceImpl implements RetentionSupportCommandService {

    private final RetentionSupportRepository supportRepository;
    private final EmployeeRepository employeeRepository;
    private final RetentionContactCommandService retentionContactCommandService;
    private final ManagerFinderService managerFinderService;

    @Override
    public void saveAll(List<RetentionSupport> supports) {
        log.info("API 호출 시작 - saveAll, 요청 파라미터: supportsCount={}", supports.size());

        // 저장
        supportRepository.saveAllSupports(supports);

        // 자동 면담 신청
        for (RetentionSupport support : supports) {
            int score = support.getRetentionScore().intValue();
            StabilityType stability = StabilityType.fromScore(score);

            if (stability == StabilityType.SEVERE) {
                try {
                    Employee emp = employeeRepository.findByEmpId(support.getEmpId())
                            .orElseThrow(() -> new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND));

                    Optional<Long> managerIdOpt = managerFinderService.findManagerIdForEmp(emp);

                    Long managerId = managerIdOpt
                            .filter(id -> !id.equals(emp.getEmpId()))
                            .orElseThrow(() -> new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND));

                    RetentionContactCreateDto dto = RetentionContactCreateDto.builder()
                            .targetId(emp.getEmpId())
                            .managerId(managerId)
                            .writerId(managerId)
                            .reason("자동 생성 - 근속 지수 심각")
                            .build();

                    retentionContactCommandService.createContact(dto);
                    log.info("자동 면담 생성 완료 - targetId={}, managerId={}", emp.getEmpId(), managerId);

                } catch (Exception e) {
                    log.warn("자동 면담 생성 실패 - empId={}, 사유={}", support.getEmpId(), e.getMessage());
                    throw e; // 트랜잭션 롤백
                }
            }
        }

        log.info("API 호출 성공 - saveAll, 근속 지원 저장 완료 - savedCount={}", supports.size());
    }
}
