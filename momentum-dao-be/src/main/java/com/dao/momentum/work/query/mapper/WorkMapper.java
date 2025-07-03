package com.dao.momentum.work.query.mapper;

import com.dao.momentum.work.query.dto.request.AdminWorkSearchDTO;
import com.dao.momentum.work.query.dto.request.WorkSearchDTO;
import com.dao.momentum.work.query.dto.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WorkMapper {
    List<MyWorkDTO> getMyWorks(WorkSearchDTO request, long empId);

    List<WorkDTO> getWorks(@Param("request") AdminWorkSearchDTO request);

    long countWorks(@Param("request") AdminWorkSearchDTO request);

    WorkDetailsDTO getWorkDetails(long workId);

    AttendanceDTO getMyTodaysAttendance(long empId, LocalDate today, LocalDate tomorrow, List<WorkTypeName> workTypeNames, List<VacationTypeEnum> vacationTypes);

    List<WorkTypeDTO> getParentWorkTypes();

    List<WorkTypeDTO> getChildWorkTypes();

    List<VacationTypeDTO> getVacationTypes();
}
