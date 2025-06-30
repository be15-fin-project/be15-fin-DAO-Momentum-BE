package com.dao.momentum.organization.company.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.HolidayCrateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayCreateResponse;
import com.dao.momentum.organization.company.command.application.dto.response.HolidayDeleteResponse;
import com.dao.momentum.organization.company.command.domain.aggregate.Holiday;
import com.dao.momentum.organization.company.command.domain.repository.HolidayRepository;
import com.dao.momentum.organization.company.exception.CompanyException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayCommandService {
    private final ModelMapper modelMapper;
    private final HolidayRepository holidayRepository;
    public HolidayCreateResponse createHoliday(HolidayCrateRequest request) {
        Holiday holiday = modelMapper.map(request,Holiday.class);

        //해당 날짜에 휴일 존재하는지 검사
        if(holidayRepository.existsByDate(holiday.getDate())){
            throw new CompanyException(ErrorCode.HOLIDAY_ALREADY_EXISTS);
        }

        Holiday savedHoliday = holidayRepository.save(holiday);

        return HolidayCreateResponse.builder()
                .holidayId(savedHoliday.getHolidayId())
                .message("휴일이 등록되었습니다.")
                .build();
    }

    @Transactional
    public HolidayDeleteResponse deleteHoliday(Integer holidayId) {
        if(!holidayRepository.existsByHolidayId(holidayId)){
            throw new CompanyException(ErrorCode.HOLIDAY_NOT_FOUND);
        }
        holidayRepository.deleteByHolidayId(holidayId);

        return HolidayDeleteResponse.builder()
                .holidayId(holidayId)
                .message("휴일이 삭제되었습니다.")
                .build();
    }
}
