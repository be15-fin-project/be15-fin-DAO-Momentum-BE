package com.dao.momentum.evaluation.eval.query.mapper;

import java.util.List;

import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationTaskRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluatorRoleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationTaskResponseDto;

@Mapper
public interface EvaluationTaskMapper {

    //최신 라운드 번호 조회
    int findLatestRoundNo();

    // 평가 태스크 목록 조회 (SELF, ORG, PEER 통합)
    List<EvaluationTaskResponseDto> findTasks(@Param("req") EvaluationTaskRequestDto req,
                                              @Param("empId") Long empId,
                                              @Param("roundNo") int roundNo,
                                              @Param("evaluator") EvaluatorRoleDto evaluator);


    // 전체 태스크 건수 조회 (페이징)
    int countTasks(@Param("req") EvaluationTaskRequestDto req,
                   @Param("empId") Long empId,
                   @Param("roundNo") int roundNo,
                   @Param("evaluator") EvaluatorRoleDto evaluator);


    // 사용자 권한 조회
    EvaluatorRoleDto findEvaluatorRole(Long empId);

}