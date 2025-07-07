package com.dao.momentum.approve.query.mapper;

import com.dao.momentum.approve.query.dto.EmployeeLeaderDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApproveMapper {

    EmployeeLeaderDto findEmployeeLeader(Long empId);

}
