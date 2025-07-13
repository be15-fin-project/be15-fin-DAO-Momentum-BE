package com.dao.momentum.evaluation.hr.command.application.service;


import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionDeleteResponse;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrObjectionRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrObjectionServiceImpl implements HrObjectionService {

    private final HrObjectionRepository objectionRepository;

    private static final int DEFAULT_STATUS_ID = 1;

    @Override
    @Transactional
    public HrObjectionCreateResponse create(HrObjectionCreateDto dto, Long empId) {
        log.info("[HrObjectionServiceImpl] create() 호출 시작 - empId={}, resultId={}, 요청 파라미터={}",
                empId, dto.resultId(), dto);

        // 1. 중복 제출 방지
        if (objectionRepository.existsByResultIdAndIsDeleted(dto.resultId(), UseStatus.N)) {
            throw new HrException(ErrorCode.ALREADY_SUBMITTED_OBJECTION);
        }


        // 2. 평가 결과 존재 확인
        if (!objectionRepository.existsEvaluation(dto.resultId())) {
            log.error("존재하지 않는 평가 결과 - resultId={}", dto.resultId());
            throw new HrException(ErrorCode.EVALUATION_NOT_FOUND);
        }

        // 3. 엔티티 생성 및 저장
        HrObjection objection = HrObjection.create(dto, DEFAULT_STATUS_ID);
        HrObjection saved = objectionRepository.save(objection);

        log.info("이의 제기 저장 완료 - objectionId={}, resultId={}, writerId={}, 저장 시각={}",
                saved.getObjectionId(), dto.resultId(), empId, LocalDateTime.now());

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
        log.info("[HrObjectionServiceImpl] deleteById() 호출 시작 - objectionId={}, empId={}, 요청 파라미터={}",
                objectionId, empId, objectionId);

        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> {
                    log.error("이의 제기 정보를 찾을 수 없음 - objectionId={}", objectionId);
                    return new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND);
                });

        if (!objection.getWriterId().equals(empId)) {
            log.error("이의 제기 삭제 권한 없음 - empId={}, objectionId={}", empId, objectionId);
            throw new HrException(ErrorCode.HR_OBJECTION_FORBIDDEN);
        }

        if (!objection.getStatusId().equals(DEFAULT_STATUS_ID)) {
            log.error("이의 제기 상태가 삭제 불가 상태 - objectionId={}", objectionId);
            throw new HrException(ErrorCode.HR_OBJECTION_CANNOT_DELETE);
        }

        objection.withDraw(2);
        log.info("이의 제기 상태 변경 완료 (삭제 처리) - objectionId={}, 삭제 시각={}", objectionId, LocalDateTime.now());

        return HrObjectionDeleteResponse.builder()
                .objectionId(objectionId)
                .message("인사 평가 이의 제기가 성공적으로 삭제되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public void approve(Long objectionId, String reason) {
        log.info("[HrObjectionServiceImpl] approve() 호출 시작 - objectionId={}, 요청 파라미터={{}}",
                objectionId, reason);

        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> {
                    log.error("이의 제기 정보를 찾을 수 없음 - objectionId={}", objectionId);
                    return new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND);
                });

        objection.approve(reason);
        log.info("이의 제기 승인 처리 완료 - objectionId={}, reason={}", objectionId, reason);
    }

    @Override
    @Transactional
    public void reject(Long objectionId, String rejectReason) {
        log.info("[HrObjectionServiceImpl] reject() 호출 시작 - objectionId={}, 요청 파라미터={{}}",
                objectionId, rejectReason);

        HrObjection objection = objectionRepository.findById(objectionId)
                .orElseThrow(() -> {
                    log.error("이의 제기 정보를 찾을 수 없음 - objectionId={}", objectionId);
                    return new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND);
                });

        objection.reject(rejectReason);
        log.info("이의 제기 반려 처리 완료 - objectionId={}, rejectReason={}", objectionId, rejectReason);
    }

    @Override
    @Transactional
    public Long getResultIdByObjectionId(Long objectionId) {
        log.info("[HrObjectionServiceImpl] getResultIdByObjectionId() 호출 시작 - objectionId={}, 요청 파라미터={}",
                objectionId, objectionId);

        Long resultId = objectionRepository.findResultIdByObjectionId(objectionId)
                .orElseThrow(() -> {
                    log.error("이의 제기 결과 ID를 찾을 수 없음 - objectionId={}", objectionId);
                    return new HrException(ErrorCode.HR_OBJECTION_NOT_FOUND);
                });

        log.info("이의 제기 관련 결과 ID 조회 완료 - objectionId={}, resultId={}", objectionId, resultId);
        return resultId;
    }
}
