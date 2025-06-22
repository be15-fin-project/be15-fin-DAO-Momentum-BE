package com.dao.momentum.evaluation.hr.query.service;


import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;

public interface MyObjectionQueryService {

    // 이의 제기 내역 조회
    MyObjectionListResultDto getMyObjections(Long empId, MyObjectionListRequestDto req);
}
