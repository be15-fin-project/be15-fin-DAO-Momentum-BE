package com.dao.momentum.retention.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.command.domain.repository.RetentionContactRepository;
import com.dao.momentum.retention.exception.RetentionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetentionContactCommandServiceImpl implements RetentionContactCommandService {

    private final RetentionContactRepository repository;

    @Override
    @Transactional
    public RetentionContactResponse createContact(RetentionContactCreateDto dto) {
        if (dto.targetId().equals(dto.managerId())) {
            throw new RetentionException(ErrorCode.RETENTION_CONTACT_TARGET_EQUALS_MANAGER);
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
}
