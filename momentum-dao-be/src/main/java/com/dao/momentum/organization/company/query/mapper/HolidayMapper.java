package com.dao.momentum.organization.company.query.mapper;

import com.dao.momentum.organization.company.query.dto.request.HolidaySearchDTO;
import com.dao.momentum.organization.company.query.dto.request.HolidaySearchRequest;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HolidayMapper {
    List<HolidayGetDTO> searchHolidays(HolidaySearchDTO dto);

    long countHolidays(HolidaySearchRequest request);

    List<HolidayGetDTO> searchHolidaysPerMonth(LocalDate startDate, LocalDate endDate);
}
