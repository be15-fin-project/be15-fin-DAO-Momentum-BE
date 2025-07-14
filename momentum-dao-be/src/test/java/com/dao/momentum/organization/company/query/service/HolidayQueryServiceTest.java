package com.dao.momentum.organization.company.query.service;

import com.dao.momentum.organization.company.query.dto.request.HolidaySearchDTO;
import com.dao.momentum.organization.company.query.dto.request.HolidaySearchRequest;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetDTO;
import com.dao.momentum.organization.company.query.dto.response.HolidayGetResponse;
import com.dao.momentum.organization.company.query.dto.response.MonthPerHolidayGetResponse;
import com.dao.momentum.organization.company.query.mapper.HolidayMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayQueryServiceTest {

    @InjectMocks
    private HolidayQueryService holidayQueryService;

    @Mock
    private HolidayMapper holidayMapper;

    @Test
    void getHolidays_success() {
        // Given
        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .page(1)
                .size(2)
                .year(2025)
                .month(1)
                .build();

        HolidaySearchDTO dto = HolidaySearchDTO.from(request);

        List<HolidayGetDTO> mockHolidays = List.of(
                HolidayGetDTO.builder().holidayId(1).holidayName("신정").date(LocalDate.of(2025, 1, 1)).build(),
                HolidayGetDTO.builder().holidayId(2).holidayName("대체공휴일").date(LocalDate.of(2025, 1, 17)).build()
        );

        long totalItems = 2;

        when(holidayMapper.searchHolidays(any(HolidaySearchDTO.class))).thenReturn(mockHolidays);
        when(holidayMapper.countHolidays(request)).thenReturn(totalItems);

        // When
        HolidayGetResponse response = holidayQueryService.getHolidays(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getHolidayGetDTOList().size());
        assertEquals(1, response.getPagination().getTotalPage()); // 5 items, 2 per page → 3 pages
        assertEquals(1, response.getPagination().getCurrentPage());
        assertEquals(2, response.getPagination().getTotalItems());
    }

    @Test
    void getHolidaysPerMonth_success() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 5);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<HolidayGetDTO> mockHolidays = List.of(
                HolidayGetDTO.builder().holidayId(10).holidayName("어린이날").date(LocalDate.of(2025, 5, 5)).build(),
                HolidayGetDTO.builder().holidayId(11).holidayName("석가탄신일").date(LocalDate.of(2025, 5, 15)).build()
        );

        when(holidayMapper.searchHolidaysPerMonth(start, end)).thenReturn(mockHolidays);

        // When
        MonthPerHolidayGetResponse response = holidayQueryService.getHolidaysPerMonth(yearMonth);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getHolidayGetDTOList().size());
        assertEquals("어린이날", response.getHolidayGetDTOList().get(0).getHolidayName());
    }
}
