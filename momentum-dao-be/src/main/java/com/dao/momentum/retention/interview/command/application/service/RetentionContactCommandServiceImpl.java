package com.dao.momentum.retention.interview.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.common.kafka.producer.NotificationKafkaProducer;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactDeleteDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactFeedbackUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactResponseUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactDeleteResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactFeedbackUpdateResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactResponseUpdateResponse;
import com.dao.momentum.retention.interview.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.interview.command.domain.repository.RetentionContactRepository;
import com.dao.momentum.retention.interview.exception.InterviewException;
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
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public RetentionContactResponse createContact(RetentionContactCreateDto dto) {
        if (dto.targetId().equals(dto.managerId())) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_TARGET_EQUALS_MANAGER);
        }

        // 1. 하급자 이름 조회
        Employee targetEmployee = employeeRepository.findByEmpId(dto.targetId())
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        String targetName = targetEmployee.getName();

        // 2. 면담 엔티티 생성 및 저장
        RetentionContact contact = RetentionContact.create(
                dto.targetId(), dto.managerId(), dto.writerId(), dto.reason()
        );
        RetentionContact saved = repository.save(contact);

        // 3. 알림 메시지 생성
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

        // 4. Kafka 전송
        try {
            notificationKafkaProducer.sendNotification(saved.getManagerId().toString(), message);
            log.info("면담 ID: {}, 상급자 ID: {}, 하급자 ID: {} → 근속 면담 요청 알림 전송 완료",
                    saved.getRetentionId(), saved.getManagerId(), saved.getTargetId());
        } catch (Exception e) {
            log.error("알림 전송 실패 - 면담 ID: {}, 수신자 ID: {}, 사유: {}", saved.getRetentionId(), saved.getManagerId(), e.getMessage(), e);
        }

        // 5. 응답 반환
        return RetentionContactResponse.builder()
                .retentionId(saved.getRetentionId())
                .targetId(saved.getTargetId())
                .managerId(saved.getManagerId())
                .writerId(saved.getWriterId())
                .reason(saved.getReason())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public RetentionContactDeleteResponse deleteContact(RetentionContactDeleteDto dto) {
        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        // 이미 삭제되었는지 확인
        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        // 삭제 권한 확인: 작성자 본인 또는 관리자 권한 보유
        boolean isWriter = contact.getWriterId().equals(dto.loginEmpId());
        if (!isWriter) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_FORBIDDEN);
        }

        // 삭제 처리
        contact.markAsDeleted();

        return RetentionContactDeleteResponse.builder()
                .retentionId(contact.getRetentionId())
                .message("면담 요청이 성공적으로 삭제되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public RetentionContactResponseUpdateResponse reportResponse(RetentionContactResponseUpdateDto dto) {
        // 1. 면담 요청 존재 확인
        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        // 2. 삭제 여부 확인
        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        // 3. managerId 일치 확인
        if (!contact.getManagerId().equals(dto.loginEmpId())) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_RESPONSE_FORBIDDEN);
        }

        // 4. 면담 결과 반영
        LocalDateTime now = LocalDateTime.now();
        contact.respond(dto.response(), now);

        return RetentionContactResponseUpdateResponse.builder()
                .retentionId(contact.getRetentionId())
                .response(contact.getResponse())
                .responseAt(contact.getResponseAt())
                .build();
    }

    @Override
    @Transactional
    public RetentionContactFeedbackUpdateResponse giveFeedback(RetentionContactFeedbackUpdateDto dto) {
        // 1. 면담 요청 존재 확인
        RetentionContact contact = repository.findById(dto.retentionId())
                .orElseThrow(() -> new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND));

        // 2. 삭제 여부 확인
        if (contact.getIsDeleted().isDeleted()) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED);
        }

        // 3. 피드백 반영
        contact.giveFeedback(dto.feedback());

        return RetentionContactFeedbackUpdateResponse.builder()
                .retentionId(contact.getRetentionId())
                .feedback(contact.getFeedback())
                .build();
    }
}
