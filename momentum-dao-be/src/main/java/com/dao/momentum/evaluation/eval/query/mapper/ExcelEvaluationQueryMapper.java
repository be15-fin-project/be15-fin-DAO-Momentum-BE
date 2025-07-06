package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.PeerEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.OrgEvaluationExcelDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationExcelRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.SelfEvaluationExcelDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExcelEvaluationQueryMapper {

    List<PeerEvaluationExcelDto> selectPeerEvaluationsForExcel(PeerEvaluationExcelRequestDto request);

    List<OrgEvaluationExcelDto> selectOrgEvaluationsForExcel(OrgEvaluationExcelRequestDto request);

    List<SelfEvaluationExcelDto> selectSelfEvaluationsForExcel(SelfEvaluationExcelRequestDto request);

}
