package com.dao.momentum.evaluation.hr.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.FactorScoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EvaluationHrMapper {

    // HR 평가 목록 조회
    List<HrEvaluationItemDto> findHrEvaluations(
            @Param("empId") long empId,
            @Param("req")   MyHrEvaluationListRequestDto req
    );

    long countHrEvaluations(
            @Param("empId") long empId,
            @Param("req")   MyHrEvaluationListRequestDto req
    );

    // 특정 평가 결과에 대한 요인별 점수 조회
    List<FactorScoreDto> findFactorScores(
        @Param("resultId") long resultId
    );
}
