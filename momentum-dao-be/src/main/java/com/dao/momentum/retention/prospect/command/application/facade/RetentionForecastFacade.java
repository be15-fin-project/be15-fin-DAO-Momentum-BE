package com.dao.momentum.retention.prospect.command.application.facade;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionForecastFacade {

    private final RetentionRoundCommandService roundService;
    private final RetentionSupportCommandService supportService;
    private final RetentionInsightCommandService insightService;
    private final RetentionScoreCalculator calculator;

    private final EmployeeRepository employeeRepository;

    @Transactional
    public RetentionForecastCreateResponse createNewForecast(RetentionForecastCreateRequest request) {

        // 1. 회차 등록
        RetentionRound round = roundService.create(request.year(), request.month());

        // 2. 재직 중인 사원 조회
        List<Employee> employees = employeeRepository.findByStatus(Status.EMPLOYED);

        // 3. 각 사원별 근속 지수 계산 및 저장
        List<RetentionSupport> supports = employees.stream()
                .map(emp -> {
                    var dto = calculator.calculate(emp); // 점수 계산
                    return RetentionSupport.of(round.getRoundId(), emp.getEmpId(), dto);
                })
                .toList();

        supportService.saveAll(supports);

        // 4. 통계(근속 전망) 계산 및 저장
        List<RetentionInsight> insights = insightService.generateInsights(round.getRoundId(), supports);
        insightService.saveAll(insights);

        // 5. 응답 반환
        return RetentionForecastCreateResponse.builder()
                .roundId(round.getRoundId())
                .message("근속 전망 회차 등록 및 계산이 완료되었습니다.")
                .build();
    }
}
