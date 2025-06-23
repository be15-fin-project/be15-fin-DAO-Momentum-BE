package com.dao.momentum.retention.query.mapper;

import com.dao.momentum.retention.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.query.dto.request.RetentionSupportRaw;
import com.dao.momentum.retention.query.dto.response.RetentionForecastItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionSupportMapper {

    List<RetentionSupportRaw> findRetentionForecasts(
            @Param("req") RetentionForecastRequestDto req,
            @Param("roundNo") Integer roundNo
    );

    long countRetentionForecasts(
            @Param("req") RetentionForecastRequestDto req,
            @Param("roundNo") Integer roundNo
    );

    Integer findLatestRoundNo();
}
