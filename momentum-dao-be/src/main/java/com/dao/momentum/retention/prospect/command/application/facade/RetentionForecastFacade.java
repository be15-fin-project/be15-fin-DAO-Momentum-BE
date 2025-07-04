package com.dao.momentum.retention.prospect.command.application.facade;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.retention.prospect.command.application.calculator.RetentionScoreCalculator;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionForecastCreateRequest;
import com.dao.momentum.retention.prospect.command.application.dto.response.RetentionForecastCreateResponse;
import com.dao.momentum.retention.prospect.command.application.service.RetentionInsightCommandService;
import com.dao.momentum.retention.prospect.command.application.service.RetentionSupportCommandService;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionInsight;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionRound;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionRoundCommandService;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionRoundRepository;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionForecastFacade {

    private final RetentionRoundRepository roundRepository;
    private final RetentionRoundCommandService roundService;
    private final RetentionSupportCommandService supportService;
    private final RetentionInsightCommandService insightService;
    private final RetentionScoreCalculator calculator;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public RetentionForecastCreateResponse createNewForecast(RetentionForecastCreateRequest request) {
        log.info(">>> createNewForecast called - year={}, month={}, roundNo(optional)={}",
                request.year(), request.month(), request.roundNo());

        boolean exists = roundRepository.existsByYearAndMonth(request.year(), request.month());
        if (exists) {
            throw new ProspectException(ErrorCode.RETENTION_ROUND_ALREADY_EXIST);
        }

        int roundNo = request.roundNo() != null
                ? request.roundNo()
                : roundRepository.findMaxRoundNo().orElse(0) + 1;

        RetentionRound round = roundService.create(request.year(), request.month(), roundNo);
        log.info("회차 생성 완료 - roundId={}, roundNo={}", round.getRoundId(), roundNo);

        List<Employee> employees = employeeRepository.findByStatus(Status.EMPLOYED);
        log.info("재직 중인 사원 수: {}", employees.size());

        List<RetentionSupport> supports = employees.stream()
                .map(emp -> {
                    var dto = calculator.calculate(emp);
                    return RetentionSupport.of(round.getRoundId(), emp.getEmpId(), dto);
                })
                .toList();
        supportService.saveAll(supports);
        log.info("근속 지원 저장 완료 - count={}", supports.size());

        List<RetentionInsight> insights = insightService.generateInsights(round.getRoundId(), supports);
        insightService.saveAll(insights);
        log.info("근속 인사이트 저장 완료 - count={}", insights.size());

        RetentionForecastCreateResponse response = RetentionForecastCreateResponse.builder()
                .roundId(round.getRoundId())
                .roundNo(roundNo)
                .message("근속 전망 회차 등록 및 계산이 완료되었습니다.")
                .build();

        log.info("근속 전망 회차 등록 완료 - roundId={}, roundNo={}", response.roundId(), response.roundNo());

        return response;
    }
}
