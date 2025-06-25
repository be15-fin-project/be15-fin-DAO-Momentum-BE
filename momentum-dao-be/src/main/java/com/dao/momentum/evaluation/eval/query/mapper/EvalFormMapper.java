package com.dao.momentum.evaluation.eval.query.mapper;

import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormPromptRaw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvalFormMapper {

    List<EvalFormPromptRaw> findFormDetailByFormId(@Param("formId") Integer formId);
}
