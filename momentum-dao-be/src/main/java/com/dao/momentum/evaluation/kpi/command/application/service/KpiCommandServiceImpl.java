package com.dao.momentum.evaluation.kpi.command.application.service;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.domain.aggregate.Kpi;
import com.dao.momentum.evaluation.kpi.command.domain.repository.KpiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KpiCommandServiceImpl implements KpiCommandService {

    private final KpiRepository kpiRepository;

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
}
