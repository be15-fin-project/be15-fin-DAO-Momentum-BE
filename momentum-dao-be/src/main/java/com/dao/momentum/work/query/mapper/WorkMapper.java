package com.dao.momentum.work.query.mapper;

import com.dao.momentum.work.query.dto.response.WorkDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WorkMapper {
    List<WorkDTO> getMyWorks(LocalDate rangeStartDate, LocalDate rangeEndDate, long empId);

//    List<WorkDTO> getWorks();

}
