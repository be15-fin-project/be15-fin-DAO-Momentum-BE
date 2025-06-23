package com.dao.momentum.evaluation.hr.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
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

    // 인사 평가 상세 조회
    HrEvaluationDetailDto findEvaluationContent(@Param("resultId") Long resultId, @Param("empId") Long empId);

    RateInfo findRateInfo(@Param("resultId") Long resultId);

    WeightInfo findWeightInfo(@Param("resultId") Long resultId);

    List<FactorScoreDto> findFactorScores(@Param("resultId") Long resultId);

    RateInfo findRateInfoByRoundNo(@Param("roundNo") int roundNo);

    WeightInfo findWeightInfoByRoundNo(@Param("roundNo") int roundNo);

    int findLatestRoundNo(); // Optional: roundNo 없을 때 최신 회차용
}
