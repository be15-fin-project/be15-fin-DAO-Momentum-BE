package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExcelEvaluationQueryMapper {

    List<PeerEvaluationExcelDto> selectPeerEvaluationsForExcel(PeerEvaluationExcelRequestDto request);
}
