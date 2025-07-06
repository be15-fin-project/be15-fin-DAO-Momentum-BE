package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCancelApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiApprovalResponse;
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
public class KpiApprovalServiceImpl implements KpiApprovalService {

    private final KpiRepository kpiRepository;

    // KPI 승인 반려 처리
    @Override
    @Transactional
    public KpiApprovalResponse approveKpi(Long managerId, Long kpiId, KpiApprovalRequest request) {
        log.info("[KpiApprovalServiceImpl] approveKpi() 호출 시작 - managerId={}, kpiId={}, request={}", managerId, kpiId, request);

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> {
                    log.error("KPI 정보를 찾을 수 없음 - kpiId={}", kpiId);
                    return new KpiException(ErrorCode.KPI_NOT_FOUND);
                });

        // 상태가 'PENDING'이 아닌 경우 처리 불가
        if (!kpi.getStatusId().equals(Status.PENDING.getId())) {
            log.error("이미 처리된 KPI - kpiId={}, statusId={}", kpiId, kpi.getStatusId());
            throw new KpiException(ErrorCode.KPI_ALREADY_PROCESSED);
        }

        // 반려 사유가 없는 경우
        if (request.isRejectedWithoutReason()) {
            log.error("반려 사유가 없음 - kpiId={}", kpiId);
            throw new KpiException(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        if (request.approved()) {
            kpi.approve(request.reason()); // 상태 ACCEPTED로 변경
            log.info("KPI 승인 완료 - kpiId={}, reason={}", kpiId, request.reason());
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI가 승인되었습니다.")
                    .build();
        } else {
            kpi.reject(request.reason()); // 상태 REJECTED, 사유 기록
            log.info("KPI 반려 완료 - kpiId={}, reason={}", kpiId, request.reason());
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.REJECTED.name())
                    .message("KPI가 반려되었습니다.")
                    .build();
        }
    }

    // KPI 취소 승인/반려
    @Override
    @Transactional
    public KpiApprovalResponse approveCancelRequest(Long managerId, Long kpiId, KpiCancelApprovalRequest request) {
        log.info("[KpiApprovalServiceImpl] approveCancelRequest() 호출 시작 - managerId={}, kpiId={}, request={}", managerId, kpiId, request);

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> {
                    log.error("KPI 정보를 찾을 수 없음 - kpiId={}", kpiId);
                    return new KpiException(ErrorCode.KPI_NOT_FOUND);
                });

        // 상태가 'PENDING'이고 삭제 요청이 들어간 것만 취소 승인/반려 가능
        if (!kpi.getStatusId().equals(Status.PENDING.getId()) || !kpi.getIsDeleted().equals(UseStatus.Y)) {
            log.error("잘못된 KPI 상태 - kpiId={}, statusId={}, isDeleted={}", kpiId, kpi.getStatusId(), kpi.getIsDeleted());
            throw new KpiException(ErrorCode.KPI_INVALID_STATUS);
        }

        // 반려 사유가 없는 경우
        if (request.isRejectedWithoutReason()) {
            log.error("반려 사유가 없음 - kpiId={}", kpiId);
            throw new KpiException(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        if (request.approved()) {
            // 취소 승인: 삭제 상태 유지하면서 상태도 'ACCEPTED' 처리
            kpi.approveCancel(request.reason()); // → isDeleted = Y, status = ACCEPTED
            log.info("KPI 취소 승인 완료 - kpiId={}, reason={}", kpiId, request.reason());
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI 취소가 승인되었습니다.")
                    .build();
        } else {
            // 취소 반려: 삭제 상태 해제 및 반려 처리
            kpi.rejectCancel(request.reason()); // → isDeleted = N, status = ACCEPTED, reason = 사유
            log.info("KPI 취소 반려 완료 - kpiId={}, reason={}", kpiId, request.reason());
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI 취소가 반려되었습니다.")
                    .build();
        }
    }
}
