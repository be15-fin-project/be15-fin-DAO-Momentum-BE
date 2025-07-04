package com.dao.momentum.evaluation.kpi.query.mapper;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiExelRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiExcelDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KpiExcelMapper {

    List<KpiExcelDto> selectKpisForExcel(KpiExelRequestDto request);
}
