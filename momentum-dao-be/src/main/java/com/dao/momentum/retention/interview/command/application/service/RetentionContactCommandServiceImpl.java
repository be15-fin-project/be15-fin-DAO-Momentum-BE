package com.dao.momentum.retention.interview.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RetentionContactCommandServiceImpl implements RetentionContactCommandService {

    private final RetentionContactRepository repository;

    @Override
    @Transactional
    public RetentionContactResponse createContact(RetentionContactCreateDto dto) {
        if (dto.targetId().equals(dto.managerId())) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_TARGET_EQUALS_MANAGER);
        }
        // 1. 엔티티 생성
        RetentionContact contact = RetentionContact.create(
                dto.targetId(),
                dto.managerId(),
                dto.writerId(),
                dto.reason()
        );

        // 2. 저장
        RetentionContact saved = repository.save(contact);

        // 3. 응답 DTO 변환
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
