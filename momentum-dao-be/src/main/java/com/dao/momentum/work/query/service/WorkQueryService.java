package com.dao.momentum.work.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.organization.company.query.service.HolidayQueryService;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchDTO;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchDTO;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.*;
import com.dao.momentum.work.query.mapper.WorkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkQueryService {

    private final WorkMapper workMapper;
    private final HolidayQueryService holidayQueryService;

    @Transactional(readOnly = true)
    public WorkListResponse getMyWorks(UserDetails userDetails, WorkSearchRequest workSearchRequest) {
        long empId = Long.parseLong(userDetails.getUsername());

        WorkSearchDTO workSearchDTO = WorkSearchDTO.fromRequest(workSearchRequest);

        List<WorkDTO> works = workMapper.getMyWorks(workSearchDTO, empId);

        return WorkListResponse.builder()
                .works(works)
                .pagination(null)
                .build();
    }

    @Transactional(readOnly = true)
    public WorkListResponse getWorks(AdminWorkSearchRequest adminWorkSearchRequest) {

        AdminWorkSearchDTO adminWorkSearchDTO = AdminWorkSearchDTO.fromRequest(adminWorkSearchRequest);

        List<WorkDTO> works = workMapper.getWorks(adminWorkSearchDTO);
        int page = adminWorkSearchDTO.getPage();
        int size = adminWorkSearchDTO.getSize();

        long totalItems = workMapper.countWorks(adminWorkSearchDTO);

        return WorkListResponse.builder()
                .works(works)
                .pagination(
                        Pagination.builder()
                                .currentPage(page)
                                .totalItems(totalItems)
                                .totalPage((int) Math.ceil((double) totalItems / size))
                                .build()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public WorkDetailsResponse getWorkDetails(long workId) {
        WorkDetailsDTO workDetails = workMapper.getWorkDetails(workId);

        return WorkDetailsResponse.builder()
                .workDetails(workDetails)
                .build();
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getMyTodaysAttendance(UserDetails userDetails, LocalDate today) {
        long empId = Long.parseLong(userDetails.getUsername());
        LocalDate tomorrow = today.plusDays(1);

        AttendanceDTO attendance = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.WORK), null);
        AttendanceDTO amHalfDayoff = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.VACATION), List.of(VacationTypeEnum.AM_HALF_DAYOFF));
        AttendanceDTO pmHalfDayoff = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.VACATION), List.of(VacationTypeEnum.PM_HALF_DAYOFF));
        AttendanceDTO dayoff = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.VACATION), List.of(VacationTypeEnum.DAYOFF));

        List<VacationTypeEnum> vacationTypes = Arrays.stream(VacationTypeEnum.values())
                .filter(v -> v != VacationTypeEnum.DAYOFF
                        && v != VacationTypeEnum.PM_HALF_DAYOFF
                        && v != VacationTypeEnum.AM_HALF_DAYOFF)
                .toList();

        AttendanceDTO vacation = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.VACATION), vacationTypes);
        AttendanceDTO approvedWork = workMapper.getMyTodaysAttendance(empId, today, tomorrow, List.of(WorkTypeName.REMOTE_WORK, WorkTypeName.BUSINESS_TRIP), null);

        boolean isWeekend = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(today.getDayOfWeek());

        boolean isHoliday = holidayQueryService.isHoliday(today);

        return AttendanceResponse.builder()
                .attendance(attendance)
                .amHalfDayoff(amHalfDayoff)
                .pmHalfDayoff(pmHalfDayoff)
                .dayoff(dayoff)
                .approvedWork(approvedWork)
                .vacation(vacation)
                .isWeekend(isWeekend)
                .isHoliday(isHoliday)
                .empId(empId)
                .build();
    }

    public WorkTypeResponse getWorkTypes() {
        List<WorkTypeDTO> parentWorkTypes = workMapper.getParentWorkTypes();

        List<WorkTypeDTO> childWorkTypes = workMapper.getChildWorkTypes();

        return WorkTypeResponse.builder()
                .parentWorkTypes(parentWorkTypes)
                .childWorkTypes(childWorkTypes)
                .build();
    }

    public VacationTypeResponse getVacationTypes() {
        List<VacationTypeDTO> vacationTypes = workMapper.getVacationTypes();

        return VacationTypeResponse.builder()
                .vacationTypes(vacationTypes)
                .build();
    }
}