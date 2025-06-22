package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.dto.response.ObjectionListResultDto;

public interface MyObjectionQueryService {

    // 이의 제기 목록 조회
    MyObjectionListResultDto getMyObjections(Long empId, MyObjectionListRequestDto req);

    // 이의 제기 상세 조회
    ObjectionListResultDto getObjectionDetail(Long objectionId);
}
