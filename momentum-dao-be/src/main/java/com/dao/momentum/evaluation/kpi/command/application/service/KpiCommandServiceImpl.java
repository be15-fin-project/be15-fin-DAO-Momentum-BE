package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiProgressUpdateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiProgressUpdateResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KpiCommandServiceImpl implements KpiCommandService {

    private final KpiRepository kpiRepository;

    // KPI 제출
    @Override
    @Transactional
    public KpiCreateResponse createKpi(Long empId, KpiCreateDTO dto) {
        Kpi kpi = Kpi.applyCreateDTO(dto, empId);
        Kpi saved = kpiRepository.save(kpi);

        return KpiCreateResponse.builder()
                .kpiId(saved.getKpiId())
                .message("KPI가 성공적으로 생성되었습니다.")
                .build();
    }

    // KPI 취소 요청
    @Override
    @Transactional
    public CancelKpiResponse cancelKpi(Long empId, Long kpiId, String reason) {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new KpiException(ErrorCode.KPI_NOT_FOUND));

        if (!kpi.getEmpId().equals(empId)) {
            throw new KpiException(ErrorCode.KPI_REQUEST_FORBIDDEN);
        }

        if (!kpi.getStatusId().equals(Status.ACCEPTED.getId()) || kpi.getIsDeleted() == UseStatus.Y) {
            throw new KpiException(ErrorCode.KPI_INVALID_STATUS);
        }

        kpi.cancel(reason); // Entity 상태 변경
        Kpi saved = kpiRepository.save(kpi); // 변경 후 저장

        return CancelKpiResponse.builder()
                .kpiId(saved.getKpiId())
                .message("KPI가 성공적으로 취소 요청되었습니다.")
                .build();
    }

    // KPI 진척도 수정
    @Override
    @Transactional
    public KpiProgressUpdateResponse updateProgress(Long empId, Long kpiId, KpiProgressUpdateRequest request) {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new KpiException(ErrorCode.KPI_NOT_FOUND));

        if (!kpi.getEmpId().equals(empId)) {
            throw new KpiException(ErrorCode.KPI_REQUEST_FORBIDDEN);
        }

        if (request.getProgress() < 0 || request.getProgress() > 100) {
            throw new KpiException(ErrorCode.KPI_EDIT_FORBIDDEN);
        }


        kpi.updateProgress(request.getProgress());

        return KpiProgressUpdateResponse.builder()
                .kpiId(kpi.getKpiId())
                .progress(kpi.getKpiProgress())
                .message("KPI 진척도가 성공적으로 업데이트되었습니다.")
                .build();
    }
}
