package com.dao.momentum.retention.prospect.query.mapper;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportRaw;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionSupportDetailDto;
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

    // 근속 전망 상세 조회
    RetentionSupportDetailDto findSupportDetail(@Param("retentionId") Long retentionId);

}
