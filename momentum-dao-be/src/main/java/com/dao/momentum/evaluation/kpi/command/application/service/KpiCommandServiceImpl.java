package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiProgressUpdateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiProgressUpdateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiWithdrawResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiCommandServiceImpl implements KpiCommandService {

    private final KpiRepository kpiRepository;

    // KPI 제출
    @Override
    @Transactional
    public KpiCreateResponse createKpi(Long empId, KpiCreateDTO dto) {
        log.info("[KpiCommandServiceImpl] createKpi() 호출 시작 - empId={}, dto={}", empId, dto);

        Kpi kpi = Kpi.applyCreateDTO(dto, empId);
        Kpi saved = kpiRepository.save(kpi);

        log.info("KPI 생성 완료 - kpiId={}, empId={}, message=KPI가 성공적으로 생성되었습니다.", saved.getKpiId(), empId);

        return KpiCreateResponse.builder()
                .kpiId(saved.getKpiId())
                .message("KPI가 성공적으로 생성되었습니다.")
                .build();
    }

    // KPI 철회
    @Override
    @Transactional
    public KpiWithdrawResponse withdrawKpi(Long empId, Long kpiId) {
        log.info("[KpiCommandServiceImpl] withdrawKpi() 호출 - empId={}, kpiId={}", empId, kpiId);

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> {
                    log.error("KPI 정보 없음 - kpiId={}", kpiId);
                    throw new KpiException(ErrorCode.KPI_NOT_FOUND);
                });

        if (!kpi.getEmpId().equals(empId)) {
            log.error("KPI 철회 권한 없음 - empId={}, kpiId={}", empId, kpiId);
            throw new KpiException(ErrorCode.KPI_REQUEST_FORBIDDEN);
        }

        if (!kpi.getStatusId().equals(Status.PENDING.getId()) || kpi.getIsDeleted() == UseStatus.Y) {
            log.error("철회 불가능한 KPI 상태 - kpiId={}, statusId={}, isDeleted={}", kpiId, kpi.getStatusId(), kpi.getIsDeleted());
            throw new KpiException(ErrorCode.KPI_INVALID_STATUS);
        }

        // 논리 삭제 처리
        kpi.withdraw();
        kpiRepository.save(kpi);

        log.info("KPI 철회 완료 - kpiId={}", kpiId);
        return KpiWithdrawResponse.builder()
                .kpiId(kpiId)
                .message("KPI가 성공적으로 철회되었습니다.")
                .build();
    }


    // KPI 취소 요청
    @Override
    @Transactional
    public CancelKpiResponse cancelKpi(Long empId, Long kpiId, String reason) {
        log.info("[KpiCommandServiceImpl] cancelKpi() 호출 시작 - empId={}, kpiId={}, reason={}", empId, kpiId, reason);

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> {
                    log.error("KPI 정보 없음 - kpiId={}", kpiId);
                    return new KpiException(ErrorCode.KPI_NOT_FOUND);
                });

        if (!kpi.getEmpId().equals(empId)) {
            log.error("KPI 요청 권한 없음 - empId={}, kpiId={}", empId, kpiId);
            throw new KpiException(ErrorCode.KPI_REQUEST_FORBIDDEN);
        }

        if (!kpi.getStatusId().equals(Status.ACCEPTED.getId()) || kpi.getIsDeleted() == UseStatus.Y) {
            log.error("잘못된 KPI 상태 - kpiId={}, statusId={}, isDeleted={}", kpiId, kpi.getStatusId(), kpi.getIsDeleted());
            throw new KpiException(ErrorCode.KPI_INVALID_STATUS);
        }

        kpi.cancel(reason); // Entity 상태 변경
        Kpi saved = kpiRepository.save(kpi); // 변경 후 저장

        log.info("KPI 취소 요청 완료 - kpiId={}, message=KPI가 성공적으로 취소 요청되었습니다.", saved.getKpiId());

        return CancelKpiResponse.builder()
                .kpiId(saved.getKpiId())
                .message("KPI가 성공적으로 취소 요청되었습니다.")
                .build();
    }

    // KPI 진척도 수정
    @Override
    @Transactional
    public KpiProgressUpdateResponse updateProgress(Long empId, Long kpiId, KpiProgressUpdateRequest request) {
        log.info("[KpiCommandServiceImpl] updateProgress() 호출 시작 - empId={}, kpiId={}, progress={}", empId, kpiId, request.progress());

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> {
                    log.error("KPI 정보 없음 - kpiId={}", kpiId);
                    return new KpiException(ErrorCode.KPI_NOT_FOUND);
                });

        if (!kpi.getEmpId().equals(empId)) {
            log.error("KPI 요청 권한 없음 - empId={}, kpiId={}", empId, kpiId);
            throw new KpiException(ErrorCode.KPI_REQUEST_FORBIDDEN);
        }

        if (request.progress() < 0 || request.progress() > 100) {
            log.error("잘못된 진척도 값 - kpiId={}, progress={}", kpiId, request.progress());
            throw new KpiException(ErrorCode.KPI_EDIT_FORBIDDEN);
        }

        kpi.updateProgress(request.progress());
        log.info("KPI 진척도 업데이트 완료 - kpiId={}, newProgress={}", kpiId, kpi.getKpiProgress());

        return KpiProgressUpdateResponse.builder()
                .kpiId(kpi.getKpiId())
                .progress(kpi.getKpiProgress())
                .message("KPI 진척도가 성공적으로 업데이트되었습니다.")
                .build();
    }
}
