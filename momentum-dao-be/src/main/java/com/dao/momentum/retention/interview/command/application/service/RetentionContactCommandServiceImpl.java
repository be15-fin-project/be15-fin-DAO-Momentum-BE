package com.dao.momentum.retention.interview.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.common.kafka.producer.NotificationKafkaProducer;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRolesRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.service.ManagerFinderService;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactDeleteDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactFeedbackUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactResponseUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.response.*;
import com.dao.momentum.retention.interview.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.interview.command.domain.repository.RetentionContactRepository;
import com.dao.momentum.retention.interview.exception.InterviewException;
import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionSupportRepository;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionContactCommandServiceImpl implements RetentionContactCommandService {

    private final RetentionContactRepository repository;
    private final RetentionSupportRepository supportRepository;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRolesRepository employeeRolesRepository;
    private final ManagerFinderService managerFinderService;


    @Override
    @Transactional
    public RetentionContactResponse createContact(RetentionContactCreateDto dto) {
        log.info("API 호출 시작 - createContact, 요청 파라미터: targetId={}, managerId={}, writerId={}, reason={}",
                dto.targetId(), dto.managerId(), dto.writerId(), dto.reason());

        if (dto.targetId().equals(dto.managerId())) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_TARGET_EQUALS_MANAGER);
        }

        // 팀장 권한 확인
        Integer count = employeeRolesRepository.countManagerRole(dto.managerId(), 4L);
        log.info("managerId={}의 팀장 role count = {}", dto.managerId(), count);
        if (count <= 0) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_WRITER_NOT_MANAGER);
        }


        Employee targetEmployee = employeeRepository.findByEmpId(dto.targetId())
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        String targetName = targetEmployee.getName();

        RetentionContact contact = RetentionContact.create(
                dto.targetId(), dto.managerId(), dto.writerId(), dto.reason()
        );
        RetentionContact saved = repository.save(contact);

        String content = String.format(
                "[근속 리스크 알림]\n팀원 %s님의 근속 전망이 낮게 분석되었습니다.\n리스크 완화를 위해 조속한 면담을 권장드립니다.",
                targetName
        );

        NotificationMessage message = NotificationMessage.builder()
                .content(content)
                .type("RETENTION_CONTACT")
                .url("/retention-support/communication-requests")
                .receiverId(saved.getManagerId())
                .contactId(saved.getRetentionId())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            notificationKafkaProducer.sendNotification(saved.getManagerId().toString(), message);
            log.info("면담 알림 전송 완료 - retentionId={}, managerId={}, targetId={}",
                    saved.getRetentionId(), saved.getManagerId(), saved.getTargetId());
        } catch (Exception e) {
            log.error("알림 전송 실패 - 면담 ID: {}, 수신자 ID: {}, 사유: {}", saved.getRetentionId(), saved.getManagerId(), e.getMessage(), e);
        }

        RetentionContactResponse response = RetentionContactResponse.builder()
                .retentionId(saved.getRetentionId())
                .targetId(saved.getTargetId())
                .managerId(saved.getManagerId())
                .writerId(saved.getWriterId())
                .reason(saved.getReason())
                .createdAt(saved.getCreatedAt())
                .build();

        log.info("API 호출 성공 - createContact, 생성 완료 - retentionId={}", response.retentionId());
        return response;
    }

    @Override
    @Transactional
    public RetentionContactDeleteResponse deleteContact(RetentionContactDeleteDto dto) {
        log.info("API 호출 시작 - deleteContact, 요청 파라미터: retentionId={}, loginEmpId={}",
                dto.retentionId(), dto.loginEmpId());

        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        boolean isWriter = contact.getWriterId().equals(dto.loginEmpId());
        if (!isWriter) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_FORBIDDEN);
        }

        contact.markAsDeleted();

        log.info("API 호출 성공 - deleteContact, 삭제 완료 - retentionId={}", contact.getRetentionId());

        return new RetentionContactDeleteResponse(contact.getRetentionId(), "면담 요청이 성공적으로 삭제되었습니다.");

    }

    @Override
    @Transactional
    public RetentionContactResponseUpdateResponse reportResponse(RetentionContactResponseUpdateDto dto) {
        log.info("API 호출 시작 - reportResponse, 요청 파라미터: retentionId={}, loginEmpId={}, response={}",
                dto.retentionId(), dto.loginEmpId(), dto.response());

        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        if (!contact.getManagerId().equals(dto.loginEmpId())) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_RESPONSE_FORBIDDEN);
        }

        contact.respond(dto.response(), LocalDateTime.now());

        log.info("API 호출 성공 - reportResponse, 응답 완료 - retentionId={}", contact.getRetentionId());

        return RetentionContactResponseUpdateResponse.builder()
                .retentionId(contact.getRetentionId())
                .response(contact.getResponse())
                .responseAt(contact.getResponseAt())
                .build();
    }

    @Override
    @Transactional
    public RetentionContactFeedbackUpdateResponse giveFeedback(RetentionContactFeedbackUpdateDto dto) {
        log.info("API 호출 시작 - giveFeedback, 요청 파라미터: retentionId={}, feedback={}",
                dto.retentionId(), dto.feedback());

        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        contact.giveFeedback(dto.feedback());

        log.info("API 호출 성공 - giveFeedback, 피드백 완료 - retentionId={}", contact.getRetentionId());

        return RetentionContactFeedbackUpdateResponse.builder()
                .retentionId(contact.getRetentionId())
                .feedback(contact.getFeedback())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactManagerInfoResponse getManagerInfoByRetentionId(Long retentionId) {
        log.info("API 호출 시작 - getManagerInfoByRetentionId, retentionId={}", retentionId);

        // 1. 근속 전망(retention_support) 정보 조회
        RetentionSupport support = supportRepository.findById(retentionId)
                .orElseThrow(() -> new ProspectException(ErrorCode.RETENTION_FORECAST_NOT_FOUND));

        Long empId = support.getEmpId();

        // 2. 사원 정보 조회
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        Integer targetDeptId = employee.getDeptId();

        // 3. 상급자 ID 조회
        Long managerId = managerFinderService.findManagerIdForEmp(employee)
                .filter(id -> !id.equals(empId))
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_MANAGER_NOT_FOUND));

        // 4. 상급자 정보 조회
        Employee manager = employeeRepository.findByEmpId(managerId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        Integer managerDeptId = manager.getDeptId();

        log.info("대상자 및 상급자 조회 성공 - targetId={}, managerId={}, targetDeptId={}, managerDeptId={}",
                empId, managerId, targetDeptId, managerDeptId);

        return RetentionContactManagerInfoResponse.builder()
                .targetId(empId)
                .targetDeptId(targetDeptId)
                .managerId(managerId)
                .managerDeptId(managerDeptId)
                .build();
    }

}
