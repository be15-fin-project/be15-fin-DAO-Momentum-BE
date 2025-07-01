package com.dao.momentum.approve.query.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VacationMapper {
    int getRemainingDayoffs(long empId);

    int getRemainingRefreshs(long empId);
}
