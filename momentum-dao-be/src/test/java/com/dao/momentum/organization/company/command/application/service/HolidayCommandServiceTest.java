package com.dao.momentum.organization.company.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.HolidayCrateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayCreateResponse;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayDeleteResponse;
import com.dao.momentum.organization.company.command.domain.aggregate.Holiday;
import com.dao.momentum.organization.company.command.domain.repository.HolidayRepository;
import com.dao.momentum.organization.company.exception.CompanyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayCommandServiceTest {

    @InjectMocks
    private HolidayCommandService holidayCommandService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createHoliday_success() {
        // Given
        HolidayCrateRequest request = HolidayCrateRequest.builder()
                .date(LocalDate.of(2025, 8, 15))
                .holidayName("광복절")
                .build();

        Holiday holiday = Holiday.builder()
                .date(request.getDate())
                .holidayName(request.getHolidayName())
                .build();

        Holiday savedHoliday = Holiday.builder()
                .holidayId(1)
                .date(request.getDate())
                .holidayName(request.getHolidayName())
                .build();

        when(modelMapper.map(request, Holiday.class)).thenReturn(holiday);
        when(holidayRepository.existsByDate(holiday.getDate())).thenReturn(false);
        when(holidayRepository.save(holiday)).thenReturn(savedHoliday);

        // When
        HolidayCreateResponse response = holidayCommandService.createHoliday(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getHolidayId());
        assertEquals("휴일이 등록되었습니다.", response.getMessage());
        verify(holidayRepository).save(holiday);
    }

    @Test
    void createHoliday_alreadyExists_throwsException() {
        // Given
        HolidayCrateRequest request = HolidayCrateRequest.builder()
                .holidayName("신정")
                .date(LocalDate.of(2025, 1, 1))
                .build();

        Holiday holiday = Holiday.builder()
                .date(request.getDate())
                .holidayName(request.getHolidayName())
                .build();

        when(modelMapper.map(request, Holiday.class)).thenReturn(holiday);
        when(holidayRepository.existsByDate(holiday.getDate())).thenReturn(true);

        // When & Then
        CompanyException exception = assertThrows(CompanyException.class, () ->
                holidayCommandService.createHoliday(request));
        assertEquals(ErrorCode.HOLIDAY_ALREADY_EXISTS, exception.getErrorCode());
        verify(holidayRepository, never()).save(any());
    }

    @Test
    void deleteHoliday_success() {
        // Given
        Integer holidayId = 10;
        when(holidayRepository.existsByHolidayId(holidayId)).thenReturn(true);

        // When
        HolidayDeleteResponse response = holidayCommandService.deleteHoliday(holidayId);

        // Then
        assertNotNull(response);
        assertEquals(holidayId, response.getHolidayId());
        assertEquals("휴일이 삭제되었습니다.", response.getMessage());
        verify(holidayRepository).deleteByHolidayId(holidayId);
    }

    @Test
    void deleteHoliday_notFound_throwsException() {
        // Given
        Integer holidayId = 99;
        when(holidayRepository.existsByHolidayId(holidayId)).thenReturn(false);

        // When & Then
        CompanyException exception = assertThrows(CompanyException.class, () ->
                holidayCommandService.deleteHoliday(holidayId));
        assertEquals(ErrorCode.HOLIDAY_NOT_FOUND, exception.getErrorCode());
        verify(holidayRepository, never()).deleteByHolidayId(any());
    }
}
