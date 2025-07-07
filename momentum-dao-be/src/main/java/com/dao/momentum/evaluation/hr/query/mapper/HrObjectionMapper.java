package com.dao.momentum.evaluation.hr.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HrObjectionMapper {

    // 팀장이 작성한 사원의 인사 평가 이의제기 목록 조회
    List<HrObjectionItemDto> findObjections(HrObjectionListRequestDto req);

    // 페이징을 위한 전체 카운트
    long countObjections(HrObjectionListRequestDto req);

    // 인사 평가 이의 제기 상세 조회

    // 이의 제기 상세 조회
    ObjectionItemDto findObjectionDetail(
            @Param("objectionId") Long objectionId
    );

    RateInfo findRateInfo(@Param("resultId") Long resultId);

    WeightInfo findWeightInfo(@Param("resultId") Long resultId);

    List<FactorScoreDto> findFactorScores(@Param("resultId") Long resultId);
}
