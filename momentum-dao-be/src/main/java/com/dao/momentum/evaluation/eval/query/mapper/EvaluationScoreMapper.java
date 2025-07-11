package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.hr.query.dto.response.RateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationScoreMapper {

    // 특정 평가 항목(formId)에 대해 피평가자(empId)의 점수 목록을 조회
    List<Integer> findScoresByFormIdAndTarget(
        @Param("formId") int formId,
        @Param("empId") Long empId
    );

    // 특정 회차의 해당 사원의 인사 평가 점수
    Integer findHrScoreByRoundIdAndEmpId(@Param("roundId") Long roundId, @Param("empId") Long empId);

    // 특정 회차의 인사 평가 전체 대상자 점수 목록 조회
    List<Integer> findAllHrScoresByRoundId(@Param("roundId") Long roundId);

    // 최근 2회 인사 평가 회차 ID 조회
    List<Long> findRecentHrRoundIdsByEmpId(@Param("empId") Long empId);

    // 회차별 등급 비율 조회
    RateInfo findRateInfoByRoundId(@Param("roundId") Long roundId);

}
