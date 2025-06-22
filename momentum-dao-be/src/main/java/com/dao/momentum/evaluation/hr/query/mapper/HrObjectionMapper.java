package com.dao.momentum.evaluation.hr.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrObjectionListResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HrObjectionMapper {

    // 팀장이 작성한 사원의 인사 평가 이의제기 목록 조회
    List<HrObjectionListResponseDto> findObjections(HrObjectionListRequestDto req);

    // 페이징을 위한 전체 카운트
    long countObjections(HrObjectionListRequestDto req);
}
