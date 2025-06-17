package com.dao.momentum.work.query.mapper;

import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkMapper {
    List<WorkDTO> getMyWorks(WorkSearchRequest request, long empId);

    List<WorkDTO> getWorks(@Param("request") AdminWorkSearchRequest request);

    long countWorks(@Param("request") AdminWorkSearchRequest request);

}
