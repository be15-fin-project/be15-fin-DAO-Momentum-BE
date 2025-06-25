package com.dao.momentum.retention.interview.query.mapper;

import com.dao.momentum.retention.interview.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.interview.query.dto.response.RetentionContactDetailDto;
import com.dao.momentum.retention.interview.query.dto.response.RetentionContactItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionContactMapper {

    // 면담 요청 내역 조회
    List<RetentionContactItemDto> findContacts(@Param("req") RetentionContactListRequestDto req);

    int countContacts(@Param("req") RetentionContactListRequestDto req);

    // 면담 상세 조회
    RetentionContactDetailDto findContactDetailById(@Param("retentionId") Long retentionId);

}
