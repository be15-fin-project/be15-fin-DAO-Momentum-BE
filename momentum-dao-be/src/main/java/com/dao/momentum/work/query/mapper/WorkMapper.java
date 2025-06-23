package com.dao.momentum.work.query.mapper;

import com.dao.momentum.work.query.dto.request.AdminWorkSearchDTO;
import com.dao.momentum.work.query.dto.request.WorkSearchDTO;
import com.dao.momentum.work.query.dto.response.WorkDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkMapper {
    List<WorkDTO> getMyWorks(WorkSearchDTO request, long empId);

    List<WorkDTO> getWorks(@Param("request") AdminWorkSearchDTO request);

    long countWorks(@Param("request") AdminWorkSearchDTO request);

}
