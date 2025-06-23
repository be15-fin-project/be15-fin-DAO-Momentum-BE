package com.dao.momentum.retention.query.mapper;

import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RetentionContactMapper {

    List<RetentionContactItemDto> findContacts(@Param("req") RetentionContactListRequestDto req);

    int countContacts(@Param("req") RetentionContactListRequestDto req);
}
