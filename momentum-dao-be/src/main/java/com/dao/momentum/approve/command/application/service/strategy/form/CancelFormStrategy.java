package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.ApproveCancelRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.domain.aggregate.ApproveCancel;
import com.dao.momentum.approve.command.domain.repository.ApproveCancelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final ApproveCancelRepository approveCancelRepository;

    public void saveDetail(JsonNode form, Long approveId) {
        // json 형식의 폼을 ApproveCancelRequest 타입으로 변경
        ApproveCancelRequest detail = objectMapper.convertValue(
                form, ApproveCancelRequest.class
        );

        // ApproveCancel 만들기
        ApproveCancel approveCancel = ApproveCancel.builder()
                .approveId(approveId)
                .cancelReason(detail.getCancelReason())
                .build();

        // 저장하기
        approveCancelRepository.save(approveCancel);
    }

}
