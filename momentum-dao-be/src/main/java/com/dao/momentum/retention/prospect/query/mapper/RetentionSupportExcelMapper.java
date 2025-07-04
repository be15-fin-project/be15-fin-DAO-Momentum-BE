package com.dao.momentum.retention.prospect.query.mapper;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportExcelDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionSupportExcelMapper {

    List<RetentionSupportExcelDto> selectSupportListForExcel(
        @Param("roundId") Long roundId,
        @Param("deptId") Long deptId,
        @Param("positionId") Long positionId,
        @Param("stabilityType") String stabilityType
    );
}
