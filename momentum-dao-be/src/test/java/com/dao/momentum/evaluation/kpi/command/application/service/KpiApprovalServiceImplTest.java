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
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpiApprovalServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KpiRepository kpiRepository;

    @InjectMocks
    private KpiApprovalServiceImpl service;

    private final Long managerId = 10L;
    private final Long kpiId = 300L;

    private Kpi mockKpi(Status status, UseStatus isDeleted) {
        return Kpi.builder()
                .kpiId(kpiId)
                .empId(1L)
                .statusId(status.getId())
                .isDeleted(isDeleted)
                .build();
    }


    @DisplayName("KPI 승인/반려 처리")
    @Nested
    class ApproveKpiTests {

        private final Long managerId = 10L;
        private final Long kpiId = 200L;

        private Kpi mockKpi(Status status, UseStatus isDeleted) {
            return Kpi.builder()
                    .kpiId(kpiId)
                    .empId(1L)
                    .statusId(status.getId())
                    .isDeleted(isDeleted)
                    .build();
        }


        @Test
        @DisplayName("작성 승인 성공")
        void approveKpi_success() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.N);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiApprovalRequest req = new KpiApprovalRequest(true, null);
            KpiApprovalResponse res = service.approveKpi(managerId, kpiId, req);

            assertThat(res.getKpiId()).isEqualTo(kpiId);
            assertThat(res.getStatus()).isEqualTo(Status.ACCEPTED.name());
            assertThat(res.getMessage()).contains("승인");
        }

        @Test
        @DisplayName("작성 반려 성공 (사유 입력됨)")
        void rejectKpi_success_withReason() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.N);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiApprovalRequest req = new KpiApprovalRequest(false, "목표 불명확");
            KpiApprovalResponse res = service.approveKpi(managerId, kpiId, req);

            assertThat(res.getKpiId()).isEqualTo(kpiId);
            assertThat(res.getStatus()).isEqualTo(Status.REJECTED.name());
            assertThat(res.getMessage()).contains("반려");
        }

        @Test
        @DisplayName("작성 반려 실패 (사유 없음)")
        void rejectKpi_fail_withoutReason() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.N);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiApprovalRequest req = new KpiApprovalRequest(false, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveKpi(managerId, kpiId, req)
            );
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        @Test
        @DisplayName("이미 처리된 KPI는 승인/반려 불가")
        void rejectKpi_fail_alreadyProcessed() {
            Kpi kpi = mockKpi(Status.ACCEPTED, UseStatus.N);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiApprovalRequest req = new KpiApprovalRequest(true, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveKpi(managerId, kpiId, req)
            );
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_ALREADY_PROCESSED);
        }

    }
    @Nested
    @DisplayName("KPI 취소 승인/반려")
    class ApproveCancelKpiTests {

        @Test
        @DisplayName("취소 승인 성공")
        void approveCancel_success() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.Y);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(true, null);
            KpiApprovalResponse res = service.approveCancelRequest(managerId, kpiId, req);

            assertThat(res.getKpiId()).isEqualTo(kpiId);
            assertThat(res.getStatus()).isEqualTo(Status.ACCEPTED.name());
            assertThat(res.getMessage()).contains("취소가 승인");
        }

        @Test
        @DisplayName("취소 반려 성공 - 사유 입력됨")
        void rejectCancel_success() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.Y);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(false, "사유 입력됨");
            KpiApprovalResponse res = service.approveCancelRequest(managerId, kpiId, req);

            assertThat(res.getKpiId()).isEqualTo(kpiId);
            assertThat(res.getStatus()).isEqualTo(Status.ACCEPTED.name());
            assertThat(res.getMessage()).contains("취소가 반려");
        }

        @Test
        @DisplayName("취소 반려 실패 - 사유 없음")
        void rejectCancel_withoutReason() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.Y);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(false, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveCancelRequest(managerId, kpiId, req)
            );

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_REJECTION_REASON_REQUIRED);
        }

        @Test
        @DisplayName("KPI 상태가 PENDING이 아니면 예외 발생")
        void invalidStatus_notPending() {
            Kpi kpi = mockKpi(Status.ACCEPTED, UseStatus.Y);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(true, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveCancelRequest(managerId, kpiId, req)
            );

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_INVALID_STATUS);
        }

        @Test
        @DisplayName("KPI 삭제 상태가 Y가 아니면 예외 발생")
        void invalidIsDeleted_notY() {
            Kpi kpi = mockKpi(Status.PENDING, UseStatus.N);
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(true, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveCancelRequest(managerId, kpiId, req)
            );

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_INVALID_STATUS);
        }

        @Test
        @DisplayName("KPI가 존재하지 않으면 예외 발생")
        void kpiNotFound() {
            when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

            KpiCancelApprovalRequest req = new KpiCancelApprovalRequest(true, null);

            KpiException ex = assertThrows(KpiException.class, () ->
                    service.approveCancelRequest(managerId, kpiId, req)
            );

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.KPI_NOT_FOUND);
        }
    }
}