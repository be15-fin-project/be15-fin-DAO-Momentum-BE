package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.SelfEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.response.OrgEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.SelfEvaluationExcelDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExcelEvaluationQueryMapper {

    List<PeerEvaluationExcelDto> selectPeerEvaluationsForExcel(PeerEvaluationExcelRequestDto request);

    List<OrgEvaluationExcelDto> selectOrgEvaluationsForExcel(OrgEvaluationExcelRequestDto request);

    List<SelfEvaluationExcelDto> selectSelfEvaluationsForExcel(SelfEvaluationExcelRequestDto request);

}
