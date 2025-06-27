package com.dao.momentum.organization.company.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.organization.company.query.dto.request.HolidaySearchDTO;
import com.dao.momentum.organization.company.query.dto.request.HolidaySearchRequest;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetDTO;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetResponse;
import com.dao.momentum.organization.company.query.dto.response.MonthPerHolidayGetResponse;
import com.dao.momentum.organization.company.query.mapper.HolidayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayQueryService {
    private final HolidayMapper holidayMapper;
    public HolidayGetResponse getHolidays(HolidaySearchRequest request) {
        HolidaySearchDTO holidaySearchDTO = HolidaySearchDTO.from(request);

        List<HolidayGetDTO> holidayGetDTOList = holidayMapper.searchHolidays(holidaySearchDTO);

        long totalItems = holidayMapper.countHolidays(request);

        int totalPage = (int) Math.ceil((double) totalItems / holidaySearchDTO.getSize());

        Pagination pagination = Pagination.builder()
                .currentPage(holidaySearchDTO.getPage())
                .totalPage(totalPage)
                .totalItems(totalItems)
                .build();

        return HolidayGetResponse.builder()
                .holidayGetDTOList(holidayGetDTOList)
                .pagination(pagination)
                .build();
    }

    @Transactional(readOnly = true)
    public MonthPerHolidayGetResponse getMonthPerHolidays(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<HolidayGetDTO> holidayGetDTOList = holidayMapper.searchHolidaysPerMonth(startDate, endDate);

        return MonthPerHolidayGetResponse.builder()
                .holidayGetDTOList(holidayGetDTOList)
                .build();
    }
}
