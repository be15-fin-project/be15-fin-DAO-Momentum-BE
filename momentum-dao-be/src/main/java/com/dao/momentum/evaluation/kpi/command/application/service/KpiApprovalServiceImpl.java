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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KpiApprovalServiceImpl implements KpiApprovalService {

    private final KpiRepository kpiRepository;

    // KPI 승인 반려 처리
    @Override
    @Transactional
    public KpiApprovalResponse approveKpi(Long managerId, Long kpiId, KpiApprovalRequest request) {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new KpiException(ErrorCode.KPI_NOT_FOUND));

        if (!kpi.getStatusId().equals(Status.PENDING.getId())) {
            throw new KpiException(ErrorCode.KPI_ALREADY_PROCESSED);
        }

        if (request.isRejectedWithoutReason()) {
            throw new KpiException(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        if (request.getApproved()) {
            kpi.approve(request.getReason()); // 상태 ACCEPTED로 변경
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI가 승인되었습니다.")
                    .build();
        } else {
            kpi.reject(request.getReason()); // 상태 REJECTED, 사유 기록
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
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new KpiException(ErrorCode.KPI_NOT_FOUND));

        // 상태가 'PENDING'이고 삭제 요청이 들어간 것만 취소 승인/반려 가능
        if (!kpi.getStatusId().equals(Status.PENDING.getId()) || !kpi.getIsDeleted().equals(UseStatus.Y)) {
            throw new KpiException(ErrorCode.KPI_INVALID_STATUS);
        }

        if (request.isRejectedWithoutReason()) {
            throw new KpiException(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        if (request.getApproved()) {
            // 취소 승인: 삭제 상태 유지하면서 상태도 'ACCEPTED' 처리
            kpi.approveCancel(request.getReason()); // → isDeleted = Y, status = ACCEPTED
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI 취소가 승인되었습니다.")
                    .build();
        } else {
            // 취소 반려: 삭제 상태 해제 및 반려 처리
            kpi.rejectCancel(request.getReason()); // → isDeleted = N, status = ACCEPTED, reason = 사유
            return KpiApprovalResponse.builder()
                    .kpiId(kpi.getKpiId())
                    .status(Status.ACCEPTED.name())
                    .message("KPI 취소가 반려되었습니다.")
                    .build();
        }
    }


}
