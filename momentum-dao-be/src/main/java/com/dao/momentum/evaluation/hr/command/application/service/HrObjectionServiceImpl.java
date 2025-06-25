package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;
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
    public HrObjectionCreateResponse create(HrObjectionCreateDto dto) {

        // 1. 중복 제출 방지
        if (objectionRepository.existsByResultId(dto.getResultId())) {
            throw new HrException(ErrorCode.ALREADY_SUBMITTED_OBJECTION);
        }

        // 2. 평가 결과 존재 확인
        if (!objectionRepository.existsEvaluation(dto.getResultId())) {
            throw new HrException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        // 4. 엔티티 생성
        HrObjection objection = HrObjection.create(dto, DEFAULT_STATUS_ID);

        // 5. 저장
        HrObjection saved = objectionRepository.save(objection);

        // 6. 응답 변환
        return HrObjectionCreateResponse.builder()
                .objectionId(saved.getObjectionId())
                .status("대기")
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
