package com.dao.momentum.retention.prospect.query.mapper;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundRawDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionRoundMapper {

    // 회차 목록 조회
    List<RetentionRoundRawDto> findRetentionRounds(@Param("req") RetentionRoundSearchRequestDto req);

    // 회차 목록 총 개수 조회 (페이징용)
    long countRetentionRounds(@Param("req") RetentionRoundSearchRequestDto req);
}
