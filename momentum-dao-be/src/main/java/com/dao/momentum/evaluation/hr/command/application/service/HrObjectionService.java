package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionDeleteResponse;

public interface HrObjectionService {

    void approve(Long objectionId, String reason);

    void reject(Long objectionId, String rejectReason);

    Long getResultIdByObjectionId(Long objectionId);

    HrObjectionCreateResponse create(HrObjectionCreateDto dto, Long empId);

    HrObjectionDeleteResponse deleteById(Long objectionId, Long empId);
}
