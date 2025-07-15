package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.VacationRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.application.validator.VacationCommandValidator;
import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.repository.ApproveRepository;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.work.command.domain.aggregate.Vacation;
import com.dao.momentum.work.command.domain.repository.VacationRepository;
import com.dao.momentum.work.command.domain.repository.VacationTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacationFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final VacationRepository approveProposalRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ApproveRepository approveRepository;
    private final VacationCommandValidator vacationCommandValidator;

    @Override
    public void saveDetail(JsonNode form, Long approveId) {
        // 1. VacationRequest 로 변환하기
        VacationRequest detail = objectMapper.convertValue(
                form, VacationRequest.class
        );

        // 2. 연반차, 리프레시 휴가인 경우 정해진 시간을 초과하지 못하게 설정
        Approve approve = approveRepository.getApproveByApproveId(approveId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

        Long empId = approve.getEmpId();
        int vacationTypeId = detail.getVacationTypeId();
        LocalDate startDate = detail.getStartDate();
        LocalDate endDate = detail.getEndDate();

        vacationCommandValidator.validateVacationLimit(empId, vacationTypeId, startDate, endDate);

        // 3. Vacation 만들기
        Vacation vacation = Vacation.builder()
                .approveId(approveId)
                .vacationTypeId(vacationTypeId)
                .startDate(startDate)
                .endDate(endDate)
                .reason(detail.getReason())
                .build();

        // 저장하기
        approveProposalRepository.save(vacation);
    }

    @Override
    public String createNotificationContent(Long approveId, String senderName, NotificationType type) {
        Vacation vacation = approveProposalRepository.findByApproveId(approveId)
                .orElseThrow(() -> new IllegalArgumentException("휴가 결재 정보가 없습니다."));

        return switch (type) {
            case REQUEST -> String.format("[결재 요청] %s님이 영수증 결재를 요청했습니다.", senderName);
            case APPROVED -> String.format("[결재 승인] %s님의 영수증 결재가 승인되었습니다.", senderName);
            case REJECTED -> String.format("[결재 반려] %s님의 영수증 결재가 반려되었습니다.", senderName);
        };
    }

}
