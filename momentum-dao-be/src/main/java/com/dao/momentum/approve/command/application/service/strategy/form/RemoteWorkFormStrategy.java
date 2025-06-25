package com.dao.momentum.approve.command.application.service.strategy.form;

import com.dao.momentum.approve.command.application.dto.request.approveType.RemoteWorkRequest;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import com.dao.momentum.work.command.domain.repository.RemoteWorkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoteWorkFormStrategy implements FormDetailStrategy {

    private final ObjectMapper objectMapper;
    private final RemoteWorkRepository remoteWorkRepository;

    public void saveDetail(JsonNode form, Long approveId) {
        // RemoteWorkRequest 로 변환하기
        RemoteWorkRequest detail = objectMapper.convertValue(
                form, RemoteWorkRequest.class
        );

        // RemoteWork 만들기
        RemoteWork remoteWork = RemoteWork.builder()
                .approveId(approveId)
                .startDate(detail.getStartDate())
                .endDate(detail.getEndDate())
                .reason(detail.getReason())
                .build();

        // 저장하기
        remoteWorkRepository.save(remoteWork);
    }

}
