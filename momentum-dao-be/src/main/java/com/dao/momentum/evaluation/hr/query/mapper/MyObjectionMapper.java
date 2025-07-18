package com.dao.momentum.evaluation.hr.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.hr.query.dto.response.ObjectionItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.RateInfo;
import com.dao.momentum.evaluation.hr.query.dto.response.WeightInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyObjectionMapper {

    // 이의 제기 내역 조회
    List<MyObjectionRaw> findMyObjections(
            @Param("empId") Long empId,
            @Param("req") MyObjectionListRequestDto req
    );
    long countMyObjections(@Param("empId") Long empId,
        @Param("req") MyObjectionListRequestDto req);

    // 이의 제기 상세 조회
    ObjectionItemDto findObjectionDetail(
            @Param("objectionId") Long objectionId
    );

    RateInfo findRateInfo(@Param("resultId") Long resultId);

    WeightInfo findWeightInfo(@Param("resultId") Long resultId);

    List<FactorScoreDto> findFactorScores(@Param("resultId") Long resultId);

    List<Integer> findAllScores(Long resultId);

}