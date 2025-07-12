package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.response.RateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationScoreMapper {

    // [1] 시점 기준 가장 최신 평가 회차 ID 조회 (특정 formId 대상)
    Long findLatestRoundIdBefore(
            @Param("formId") int formId,
            @Param("year") int year,
            @Param("month") int month
    );

    // [2] 시점 기준 최근 2개 인사 평가 회차 ID 조회
    List<Long> findRecentHrRoundIdsBefore(
            @Param("empId") Long empId,
            @Param("year") int year,
            @Param("month") int month
    );

    // [3] roundId 기반 점수 조회 (단건)
    Integer findScoreByRoundId(
            @Param("roundId") Long roundId,
            @Param("empId") Long empId
    );

    // [4] roundId 기반 인사 평가 점수 목록 조회 (수정됨)
    List<Integer> findAllHrScoresByRoundId(@Param("roundId") Long roundId);

    // [5] roundId 기반 등급 비율 조회
    RateInfo findRateInfoByRoundId(@Param("roundId") Long roundId);

    // [6] roundId 기반 인사 평가 점수 조회
    Integer findHrScoreByRoundIdAndEmpId(
            @Param("roundId") Long roundId,
            @Param("empId") Long empId
    );
}
