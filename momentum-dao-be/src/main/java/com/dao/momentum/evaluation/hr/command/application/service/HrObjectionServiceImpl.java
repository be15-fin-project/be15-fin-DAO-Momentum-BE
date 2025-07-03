package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionDeleteResponse;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrObjectionRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HrObjectionServiceImpl implements HrObjectionService {

    private final HrObjectionRepository objectionRepository;

    private static final int DEFAULT_STATUS_ID = 1;

    @Override
    @Transactional
    public HrObjectionCreateResponse create(HrObjectionCreateDto dto, Long empId) {

        // 1. 중복 제출 방지
        if (objectionRepository.existsByResultId(dto.getResultId())) {
            throw new HrException(ErrorCode.ALREADY_SUBMITTED_OBJECTION);
        }

        // 2. 평가 결과 존재 확인
        if (!objectionRepository.existsEvaluation(dto.getResultId())) {
            throw new HrException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        // 3. 엔티티 생성 및 저장
        HrObjection objection = HrObjection.create(dto, DEFAULT_STATUS_ID);
        HrObjection saved = objectionRepository.save(objection);

        return HrObjectionCreateResponse.builder()
                .objectionId(saved.getObjectionId())
                .writerId(empId)
                .status("대기")
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public HrObjectionDeleteResponse deleteById(Long objectionId, Long empId) {
        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND));

        if (!objection.getWriterId().equals(empId)) {
            throw new HrException(ErrorCode.HR_OBJECTION_FORBIDDEN);
        }

        if (!objection.getStatusId().equals(DEFAULT_STATUS_ID)) {
            throw new HrException(ErrorCode.HR_OBJECTION_CANNOT_DELETE);
        }

        objection.markAsDeleted();

        return HrObjectionDeleteResponse.builder()
                .objectionId(objectionId)
                .message("인사 평가 이의 제기가 성공적으로 삭제되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public void approve(Long objectionId, String reason) {
        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND));
        objection.approve(reason);
    }

    @Override
    @Transactional
    public void reject(Long objectionId, String rejectReason) {
        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND));
        objection.reject(rejectReason);
    }

    @Override
    @Transactional
    public Long getResultIdByObjectionId(Long objectionId) {
        return objectionRepository.findResultIdByObjectionId(objectionId)
                .orElseThrow(() -> new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND));
    }
}
