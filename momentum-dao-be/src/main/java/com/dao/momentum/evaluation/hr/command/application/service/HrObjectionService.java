package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;

public interface HrObjectionService {

    // 인사 평가 이의 제기 등록
    HrObjectionCreateResponse create(HrObjectionCreateDto dto);

    // 인사 평가 이의 제기 삭제
//    void deleteById(Long objectionId, Long requesterId);

}
